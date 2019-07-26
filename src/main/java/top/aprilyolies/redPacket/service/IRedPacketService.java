package top.aprilyolies.redPacket.service;

import top.aprilyolies.redPacket.domain.RedPacket;

public interface IRedPacketService {
    /**
     * 根据 id 获取红包
     *
     * @param id 红包 id
     * @return 红包信息
     */
    RedPacket getRedPacket(long id);

    /**
     * 递减红包
     *
     * @param id 红包 id
     * @return 影响的记录数
     */
    int decreaseRedPacket(long id);

    /**
     * 根据 id 获取红包
     *
     * @param id 红包 id
     * @return 红包信息
     */
    RedPacket getRedPacketForUpdate(long id);

    /**
     * 发红包
     *
     * @param redPacket
     * @return
     */
    int pushLuckyMoney(RedPacket redPacket);


}
