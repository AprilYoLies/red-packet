package top.aprilyolies.redPacket.service;

public interface IRedisRedPacketService {
    /**
     * 保存redis抢红包列表
     * @param redPacketId --抢红包编号
     * @param unitAmount -- 红包金额
     */
    void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount);

}
