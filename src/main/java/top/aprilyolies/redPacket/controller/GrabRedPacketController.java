package top.aprilyolies.redPacket.controller;

import top.aprilyolies.redPacket.domain.RedPacket;
import top.aprilyolies.redPacket.service.IRedPacketService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.aprilyolies.redPacket.service.IUserRedPacketService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class GrabRedPacketController {

    private IUserRedPacketService userRedPacketService;
    private IRedPacketService redPacketService;

    GrabRedPacketController(IUserRedPacketService userRedPacketService, IRedPacketService redPacketService) {
        this.userRedPacketService = userRedPacketService;
        this.redPacketService = redPacketService;
    }

    /**
     * 存在并发问题的抢红包方式
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 抢红包结果
     */
    @RequestMapping(value = "/grepRedPacket/{redPacketId}/{userId}")
    @ResponseBody   // 可能导致超发的抢红包方式
    public Map<String, Object> grepRedPacket(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> redPacket = new HashMap<>();
        int ret = userRedPacketService.grepRedPacket(redPacketId, userId);  // 先获取红包信息，然后从中得到子红包，然后保存子红包信息，不是原子操作，所以导致超发
        boolean flag = ret > 0;
        redPacket.put("success", flag);
        redPacket.put("message", flag ? "抢红包成功" : "抢红包失败");
        return redPacket;
    }

    /**
     * 悲观锁实现的抢红包方式
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 抢红包结果
     */
    @RequestMapping(value = "/grepRedPacketForUpdate/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grepRedPacketForUpdate(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> result = new HashMap<>();
        int ret = userRedPacketService.grepRedPacketForUpdate(redPacketId, userId);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "抢红包成功" : "抢红包失败");
        return result;
    }

    /**
     * 乐观锁实现抢红包方式
     *
     * @param redPacketId 红包 id
     * @param userId      用户 id
     * @return 抢红包结果
     */
    @RequestMapping(value = "/grepRedPacketByCAS/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grepRedPacketByCAS(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> result = new HashMap<>();
        int ret = userRedPacketService.grepRedPacketByCAS(redPacketId, userId);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "抢红包成功" : "抢红包失败");
        return result;
    }

    @RequestMapping(value = "/grepRedPacketByCASTime/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grepRedPacketByCASTime(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> result = new HashMap<>();
        int ret = userRedPacketService.grepRedPacketByCASTime(redPacketId, userId);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "抢红包成功" : "抢红包失败");
        return result;
    }

    @RequestMapping(value = "/grepRedPacketByCASNum/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grepRedPacketByCASNum(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> result = new HashMap<>();
        int ret = userRedPacketService.grepRedPacketByCASNum(redPacketId, userId);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "抢红包成功" : "抢红包失败");
        return result;
    }

    @RequestMapping(value = "/luckyMoney/{userId}/{amount}/{total}/{unitAmount}/{stock}")
    @ResponseBody
    public Map<String, Object> luckyMoney(@PathVariable("userId") long userId,
                                          @PathVariable("amount") double amount,
                                          @PathVariable("total") int total,
                                          @PathVariable("unitAmount") double unitAmount,
                                          @PathVariable("stock") int stock) {
        RedPacket redPacket = new RedPacket();
        redPacket.setUserId(userId);
        redPacket.setAmount(amount);
        redPacket.setStock(stock);
        redPacket.setTotal(total);
        redPacket.setUnitAmount(unitAmount);
        Map<String, Object> result = new HashMap<>();
        int ret = redPacketService.pushLuckyMoney(redPacket);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "发红包成功" : "发红包失败");
        return result;
    }


    @RequestMapping(value = "/grepRedPacketByRedis/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grepRedPacketByRedis(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> resultMap = new HashMap<>();
        long result = userRedPacketService.grepRedPacketByRedis(redPacketId, userId);
        boolean flag = result > 0;
        resultMap.put("result", flag);
        resultMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return resultMap;
    }

}
