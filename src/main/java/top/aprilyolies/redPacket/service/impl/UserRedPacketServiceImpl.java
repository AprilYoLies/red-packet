package top.aprilyolies.redPacket.service.impl;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.aprilyolies.redPacket.domain.RedPacket;
import top.aprilyolies.redPacket.domain.UserRedPacket;
import top.aprilyolies.redPacket.mapper.RedPacketMapper;
import top.aprilyolies.redPacket.mapper.UserRedPacketMapper;
import top.aprilyolies.redPacket.service.IRedisRedPacketService;
import top.aprilyolies.redPacket.service.IUserRedPacketService;

@Service
public class UserRedPacketServiceImpl implements IUserRedPacketService {

    private RedPacketMapper redPacketMapper;
    private UserRedPacketMapper userRedPacketMapper;

    UserRedPacketServiceImpl(RedPacketMapper redPacketMapper, UserRedPacketMapper userRedPacketMapper, RedisTemplate redisTemplate, IRedisRedPacketService redisRedPacketService) {
        this.redPacketMapper = redPacketMapper;
        this.userRedPacketMapper = userRedPacketMapper;
        this.redisTemplate = redisTemplate;
        this.redisRedPacketService = redisRedPacketService;
    }

    @Override   // 先获取红包信息，然后从中得到子红包，然后保存子红包信息，不是原子操作，所以导致超发
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacket(long redPacketId, long userId) {
        RedPacket redPacket = redPacketMapper.getRedPacket(redPacketId);    // 由于是先获取红包信息，所以在更新时可能导致红包超发
        if (redPacket != null && redPacket.getStock() > 0) {    // 此时红包还是满足要求的
            redPacketMapper.decreaseRedPacket(redPacketId); // 可能在满足需求的情况下，多个线程同时调用了此代码，导致红包超发
            return createUserRedPacket(redPacketId, userId, redPacket); // 根据个人得到的红包，进行更新
        } else {    // 此处两个任务持有了两个 mysql 连接，所以能够进入 if 块的线程只有总的 mysql 连接数减去二
            return 0;
        }
    }

    /**
     * 悲观锁，通过 mysql 的悲观锁保证红包不会超发
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacketForUpdate(long redPacketId, long userId) {
        RedPacket redPacket = redPacketMapper.getRedPacketForUpdate(redPacketId);
        if (redPacket != null && redPacket.getStock() > 0) {
            redPacketMapper.decreaseRedPacket(redPacketId);
            return createUserRedPacket(redPacketId, userId, redPacket);
        } else {
            return 0;
        }
    }

    // 往数据库中写入子红包数据记录
    private int createUserRedPacket(long redPacketId, long userId, RedPacket redPacket) {
        UserRedPacket userRedPacket = new UserRedPacket();
        userRedPacket.setRedPacketId(redPacketId);
        userRedPacket.setUserId(userId);
        userRedPacket.setAmount(redPacket.getUnitAmount());
        int num = redPacket.getTotal() - redPacket.getStock() + 1;  // 当前领取的是第几个红包
        userRedPacket.setNote("The " + num + "ed red packet");
        return userRedPacketMapper.grepRedPacket(userRedPacket);
    }

    /**
     * 乐观锁，大概率失败
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacketByCAS(long redPacketId, long userId) {
        RedPacket redPacket = redPacketMapper.getRedPacket(redPacketId);
        if (redPacket != null && redPacket.getStock() > 0) {
            int count = redPacketMapper.decreaseRedPacketByCAS(redPacketId, redPacket.getVersion());
            if (count != 0) {//成功
                return createUserRedPacket(redPacketId, userId, redPacket);
            } else {//失败
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 乐观锁，通过限时保证成功率
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacketByCASTime(long redPacketId, long userId) {
        long start = System.currentTimeMillis();    //记录开始时间
        while (true) {
            long end = System.currentTimeMillis();
            if (end - start > 100) {
                return 0;
            }
            RedPacket redPacket = redPacketMapper.getRedPacketForUpdate(redPacketId);
            if (redPacket != null && redPacket.getStock() > 0) {
                int count = redPacketMapper.decreaseRedPacketByCAS(redPacketId, redPacket.getVersion());
                if (count != 0) {//成功
                    return createUserRedPacket(redPacketId, userId, redPacket);
                } else {//失败
                    continue;
                }
            } else {
                return 0;
            }
        }
    }

    /**
     * 乐观锁，通过重试次数保证成功率
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacketByCASNum(long redPacketId, long userId) {
        for (int i = 0; i < 3; i++) {
            RedPacket redPacket = redPacketMapper.getRedPacketForUpdate(redPacketId);
            if (redPacket != null && redPacket.getStock() > 0) {
                int count = redPacketMapper.decreaseRedPacketByCAS(redPacketId, redPacket.getVersion());
                if (count != 0) {//成功
                    return createUserRedPacket(redPacketId, userId, redPacket);
                }
            } else {
                return 0;
            }
        }
        return 0;
    }


    private final RedisTemplate redisTemplate;

    private final IRedisRedPacketService redisRedPacketService;

    // Lua脚本
    String script = "local listKey = 'red_packet_list_'..KEYS[1] \n"
            + "local redPacket = 'red_packet_'..KEYS[1] \n"
            + "local stock = tonumber(redis.call('hget', redPacket, 'stock')) \n"
            + "if stock <= 0 then return 0 end \n"
            + "stock = stock -1 \n"
            + "redis.call('hset', redPacket, 'stock', tostring(stock)) \n"
            + "redis.call('rpush', listKey, KEYS[2]) \n"
            + "if stock == 0 then return 2 end \n"
            + "return 1 \n";

    // 在缓存LUA脚本后，使用该变量保存Redis返回的32位的SHA1编码，使用它去执行缓存的LUA脚本[加入这句话]
    String sha1 = null;

    public long grepRedPacketByRedis(Long redPacketId, Long userId) {
        // 当前抢红包用户和日期信息
        String sUId = userId + "-" + System.currentTimeMillis();
        String sPId = redPacketId + "";
        Long result;
        // 获取底层Redis操作对象
        RedisConnection jedis = redisTemplate.getConnectionFactory().getConnection();
        try {
            // 如果脚本没有加载过，那么进行加载，这样就会返回一个sha1编码
            if (sha1 == null) {
                sha1 = jedis.scriptLoad(script.getBytes());
            }
            // 执行脚本，返回结果
            Object res = jedis.evalSha(sha1, ReturnType.INTEGER, 2, sPId.getBytes(), sUId.getBytes());
            result = (Long) res;
            // 返回2时为最后一个红包，此时将抢红包信息通过异步保存到数据库中
            if (result == 2) {
                // 获取单个小红包金额
                byte[] bytes = jedis.hGet(("red_packet_" + redPacketId).getBytes(), "stock".getBytes());
                // 触发保存数据库操作
                Double unitAmount = Double.parseDouble(new String(bytes));
                System.err.println("thread_name = " + Thread.currentThread().getName());
                redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
            }
        } finally {
            // 确保jedis顺利关闭
            if (jedis != null && !jedis.isClosed()) {
                jedis.close();
            }
        }
        return result;
    }
}
