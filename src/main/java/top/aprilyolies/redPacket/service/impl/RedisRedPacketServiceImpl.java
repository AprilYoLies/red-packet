package top.aprilyolies.redPacket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.aprilyolies.redPacket.domain.UserRedPacket;
import top.aprilyolies.redPacket.service.IRedisRedPacketService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedisRedPacketServiceImpl implements IRedisRedPacketService {
    private static final String PREFIX = "red_packet_list_";
    // 每次取出1000条，避免一次取出消耗太多内存
    private static final int TIME_SIZE = 1000;

    @Autowired
    private RedisTemplate redisTemplate = null; // RedisTemplate

    @Autowired
    private DataSource dataSource = null; // 数据源

    @Override
    // 开启新线程运行
    @Async
    public void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount) {
        System.err.println("开始保存数据");
        String listKey = PREFIX + redPacketId;
        byte[] bListKey = listKey.getBytes();
        Long start = System.currentTimeMillis();
        // 获取列表操作对象
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        List<byte[]> listEles = connection.lRange(bListKey, 0, connection.lLen(bListKey));
        List<String> sListEles = new ArrayList<>(listEles.size());
        for (int i = 0; i < listEles.size(); i++) {
            sListEles.add(new String(listEles.get(i)));
        }
        List<UserRedPacket> userRedPacketList = new ArrayList<>(sListEles.size());
        for (int j = 0; j < sListEles.size(); j++) {
            String args = sListEles.get(j);
            String[] arr = args.split("-");
            String userIdStr = arr[0];
            String timeStr = arr[1];
            Long userId = Long.parseLong(userIdStr);
            Long time = Long.parseLong(timeStr);
            // 生成抢红包信息
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(unitAmount);
            userRedPacket.setGrabTime(new Timestamp(time));
            userRedPacket.setNote("The " + redPacketId + "ed red packet.");
            userRedPacketList.add(userRedPacket);
        }
        // 插入抢红包信息
        int count = executeBatch(userRedPacketList);
        // 删除Redis列表
        connection.del(listKey.getBytes());
        Long end = System.currentTimeMillis();
        System.err.println("保存数据结束，耗时" + (end - start) + "毫秒，共" + count + "条记录被保存。");
    }

    /**
     * 使用JDBC批量处理Redis缓存数据.
     *
     * @param userRedPacketList -- 抢红包列表
     * @return 抢红包插入数量.
     */
    private int executeBatch(List<UserRedPacket> userRedPacketList) {
        Connection conn = null;
        Statement stmt = null;
        int[] count = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            for (UserRedPacket userRedPacket : userRedPacketList) {
                String sql1 = "update red_packet set stock = stock-1 where id=" + userRedPacket.getRedPacketId();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sql2 = "insert into user_red_packet(red_packet_id, user_id, " + "amount, grab_time, note)"
                        + " values (" + userRedPacket.getRedPacketId() + ", " + userRedPacket.getUserId() + ", "
                        + userRedPacket.getAmount() + "," + "'" + df.format(userRedPacket.getGrabTime()) + "'," + "'"
                        + userRedPacket.getNote() + "')";
                stmt.addBatch(sql1);
                stmt.addBatch(sql2);
            }
            // 执行批量
            count = stmt.executeBatch();
            // 提交事务
            conn.commit();
        } catch (SQLException e) {
            /********* 错误处理逻辑 ********/
            throw new RuntimeException("抢红包批量执行程序错误");
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // 返回插入抢红包数据记录
        return count.length / 2;
    }
}
