package top.aprilyolies.redPacket.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserRedPacket implements Serializable {
    // 子红包 id
    private Long id;
    // 对应的红包 id
    private Long redPacketId;
    // 用户 id
    private Long userId;
    // 子红包金额
    private Double amount;
    // 子红包的获取时间
    private Timestamp grabTime;
    // 子红包的备注信息
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRedPacketId() {
        return redPacketId;
    }

    public void setRedPacketId(Long redPacketId) {
        this.redPacketId = redPacketId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Timestamp getGrabTime() {
        return grabTime;
    }

    public void setGrabTime(Timestamp grabTime) {
        this.grabTime = grabTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
