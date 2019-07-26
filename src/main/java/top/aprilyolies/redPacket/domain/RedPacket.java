package top.aprilyolies.redPacket.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class RedPacket implements Serializable {
    // 红包 id
    private Long id;
    // 发红包的用户 id
    private Long userId;
    // 红包的金额
    private Double amount;
    // 发红包的时间
    private Timestamp sendDate;
    // 红包的个数
    private Integer total;
    // 每个子红包的金额
    private Double unitAmount;
    // 剩余的子红包个数
    private Integer stock;
    // 版本号
    private Integer version;
    // 红包的备注信息
    private String note;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Timestamp getSendDate() {
        return sendDate;
    }

    public void setSendDate(Timestamp sendDate) {
        this.sendDate = sendDate;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(Double unitAmount) {
        this.unitAmount = unitAmount;
    }
}
