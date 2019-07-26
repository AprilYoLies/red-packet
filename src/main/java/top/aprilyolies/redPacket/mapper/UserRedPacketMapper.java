package top.aprilyolies.redPacket.mapper;

import top.aprilyolies.redPacket.domain.UserRedPacket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRedPacketMapper {

    /**
     * 插入抢红包信息
     *
     * @param userRedPacket 抢红包信息
     * @return 影响记录数
     */
    int grepRedPacket(UserRedPacket userRedPacket);
}
