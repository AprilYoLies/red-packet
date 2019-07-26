package top.aprilyolies.redPacket.service.impl;

import top.aprilyolies.redPacket.mapper.RedPacketMapper;
import top.aprilyolies.redPacket.domain.RedPacket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.aprilyolies.redPacket.service.IRedPacketService;

@Service
public class RedPacketServiceImpl implements IRedPacketService {

    RedPacketMapper redPacketMapper;

    RedPacketServiceImpl(RedPacketMapper redPacketMapper) {
        this.redPacketMapper = redPacketMapper;
    }

    /**
     * 读已提交隔离级别,提高数据库并发力;
     * 传播行为采用Propagation.REQUIRED,如果没有事务则会创建事务，如果有事务沿用当前事务。
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public RedPacket getRedPacket(long id) {
        return redPacketMapper.getRedPacket(id);
    }

    /**
     * 读已提交隔离级别,提高数据库并发力;
     * 传播行为采用Propagation.REQUIRED,如果没有事务则会创建事务，如果有事务沿用当前事务。
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int decreaseRedPacket(long id) {
        return redPacketMapper.decreaseRedPacket(id);
    }

    @Override
    public RedPacket getRedPacketForUpdate(long id) {
        return redPacketMapper.getRedPacketForUpdate(id);
    }

    @Override
    public int pushLuckyMoney(RedPacket redPacket) {
        return redPacketMapper.pushLuckyMoney(redPacket);
    }


}
