package top.aprilyolies.redPacket.service;

public interface IUserRedPacketService {
    /**
     * 根据 redPacketId 和 userid 获取红包信息，存在并发问题
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 红包信息
     */
    int grepRedPacket(long redPacketId, long userId);

    /**
     * 根据 redPacketId 和 userid 获取红包信息，悲观锁解决并发问题
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 红包信息
     */
    int grepRedPacketForUpdate(long redPacketId, long userId);

    /**
     * 根据 redPacketId 和 userid 获取红包信息，乐观锁下根据次数重试
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 红包信息
     */
    int grepRedPacketByCASNum(long redPacketId, long userId);

    /**
     * 根据 redPacketId 和 userid 获取红包信息，乐观锁下根据时间重试
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 红包信息
     */
    int grepRedPacketByCASTime(long redPacketId, long userId);

    /**
     * 根据 redPacketId 和 userId 获取红包信息，乐观锁解决并发问题
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 红包信息
     */
    int grepRedPacketByCAS(long redPacketId, long userId);

    /**
     * 根据 redPacketId 和 userId 获取红包信息，通过 redis 解决并发问题
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 红包信息
     */
    long grepRedPacketByRedis(Long redPacketId, Long userId);
}
