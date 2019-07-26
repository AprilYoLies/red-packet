package top.aprilyolies.redPacket.service.impl;

import top.aprilyolies.redPacket.mapper.RedPacketMapper;
import top.aprilyolies.redPacket.mapper.UserRedPacketMapper;
import top.aprilyolies.redPacket.domain.RedPacket;
import top.aprilyolies.redPacket.domain.UserRedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import top.aprilyolies.redPacket.service.IRedisRedPacketService;
import top.aprilyolies.redPacket.service.IUserRedPacketService;

@Service
public class UserRedPacketServiceImpl implements IUserRedPacketService {

    private RedPacketMapper redPacketMapper;
    private UserRedPacketMapper userRedPacketMapper;

    UserRedPacketServiceImpl(RedPacketMapper redPacketMapper, UserRedPacketMapper userRedPacketMapper) {
        this.redPacketMapper = redPacketMapper;
        this.userRedPacketMapper = userRedPacketMapper;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacket(long redPacketid, long userid) {
        RedPacket redPacket = redPacketMapper.getRedPacket(redPacketid);
        if (redPacket != null && redPacket.getStock() > 0) {
            redPacketMapper.decreaseRedPacket(redPacketid);
            return createUserRedPacket(redPacketid, userid, redPacket);
        } else {
            return 0;
        }
    }

    @Override
    /**
     * 悲观锁
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacketForUpdate(long redPacketid, long userid) {
        RedPacket redPacket = redPacketMapper.getRedPacketForUpdate(redPacketid);
        if (redPacket != null && redPacket.getStock() > 0) {
            redPacketMapper.decreaseRedPacket(redPacketid);
            return createUserRedPacket(redPacketid, userid, redPacket);
        } else {
            return 0;
        }
    }

    private int createUserRedPacket(long redPacketid, long userid, RedPacket redPacket) {
        UserRedPacket userRedPacket = new UserRedPacket();
        userRedPacket.setRedPacketId(redPacketid);
        userRedPacket.setUserId(userid);
        userRedPacket.setAmount(redPacket.getUnitAmount());
        int num = redPacket.getTotal() - redPacket.getStock() + 1;  // 当前领取的是第几个红包
        userRedPacket.setNote("The " + num + "ed read");
        return userRedPacketMapper.grepRedPacket(userRedPacket);
    }

    /**
     * 乐观锁
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacketByCAS(long redPacketid, long userid) {
        RedPacket redPacket = redPacketMapper.getRedPacketForUpdate(redPacketid);
        if (redPacket != null && redPacket.getStock() > 0) {
            int count = redPacketMapper.decreaseRedPacketByCAS(redPacketid, redPacket.getVersion());
            if (count != 0) {//成功
                return createUserRedPacket(redPacketid, userid, redPacket);
            } else {//失败
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 乐观锁
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacketByCASTime(long redPacketid, long userid) {
        long start = System.currentTimeMillis();    //记录开始时间
        while (true) {
            long end = System.currentTimeMillis();
            if (end - start > 100) {
                return 0;
            }
            RedPacket redPacket = redPacketMapper.getRedPacketForUpdate(redPacketid);
            if (redPacket != null && redPacket.getStock() > 0) {
                int count = redPacketMapper.decreaseRedPacketByCAS(redPacketid, redPacket.getVersion());
                if (count != 0) {//成功
                    return createUserRedPacket(redPacketid, userid, redPacket);
                } else {//失败
                    continue;
                }
            } else {
                return 0;
            }
        }
    }

    /**
     * 乐观锁
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grepRedPacketByCASNum(long redPacketid, long userid) {
        for (int i = 0; i < 3; i++) {
            RedPacket redPacket = redPacketMapper.getRedPacketForUpdate(redPacketid);
            if (redPacket != null && redPacket.getStock() > 0) {
                int count = redPacketMapper.decreaseRedPacketByCAS(redPacketid, redPacket.getVersion());
                if (count != 0) {//成功
                    return createUserRedPacket(redPacketid, userid, redPacket);
                }
            } else {
                return 0;
            }
        }
        return 0;
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IRedisRedPacketService redisRedPacketService;

    // Lua脚本
    String script = "local listKey = 'red_packet_list_'..KEYS[1] \n"
            + "local redPacket = 'red_packet_'..KEYS[1] \n"
            + "local stock = tonumber(redis.call('hget', redPacket, 'stock')) \n"
            + "if stock <= 0 then return 0 end \n"
            + "stock = stock -1 \n"
            + "redis.call('hset', redPacket, 'stock', tostring(stock)) \n"
            + "redis.call('rpush', listKey, ARGV[1]) \n"
            + "if stock == 0 then return 2 end \n"
            + "return 1 \n";

    // 在缓存LUA脚本后，使用该变量保存Redis返回的32位的SHA1编码，使用它去执行缓存的LUA脚本[加入这句话]
    String sha1 = null;

    @Override
    public long grepRedPacketByRedis(Long redPacketId, Long userId) {
        // 当前抢红包用户和日期信息
        String args = userId + "-" + System.currentTimeMillis();
        Long result = null;
        // 获取底层Redis操作对象
        Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        try {
            // 如果脚本没有加载过，那么进行加载，这样就会返回一个sha1编码
            if (sha1 == null) {
                sha1 = jedis.scriptLoad(script);
            }
            // 执行脚本，返回结果
            Object res = jedis.evalsha(sha1, 1, redPacketId + "", args);
            result = (Long) res;
            // 返回2时为最后一个红包，此时将抢红包信息通过异步保存到数据库中
            if (result == 2) {
                // 获取单个小红包金额
                String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
                // 触发保存数据库操作
                Double unitAmount = Double.parseDouble(unitAmountStr);
                System.err.println("thread_name = " + Thread.currentThread().getName());
                redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
            }
        } finally {
            // 确保jedis顺利关闭
            if (jedis != null && jedis.isConnected()) {
                jedis.close();
            }
        }
        return result;
    }
}
