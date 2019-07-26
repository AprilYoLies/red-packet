package top.aprilyolies.redPacket.mapper;

import top.aprilyolies.redPacket.domain.RedPacket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RedPacketMapper {

    /**
     * 获取红包信息
     *
     * @param id 红包 id
     * @return 红包具体信息
     */
    RedPacket getRedPacket(long id);

    /**
     * 扣减抢红包数
     *
     * @param id 红包 id
     * @return 更新记录条数
     */
    int decreaseRedPacket(long id);

    /**
     * 利用数据库悲观锁（独占锁）获取红包信息：锁住当前行
     *
     * @param id
     * @return
     */
    RedPacket getRedPacketForUpdate(long id);


    /**
     * 乐观锁---CAS方式
     *
     * @param id
     * @return
     */
    int decreaseRedPacketByCAS(long id, int version);

    /**
     * 发红包
     *
     * @param redPacket
     * @return
     */
    int pushLuckyMoney(RedPacket redPacket);
}
