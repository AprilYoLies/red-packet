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

    @RequestMapping(value = "/grapRedPacket/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grapRedPacket(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> redPacket = new HashMap<>();
        int ret = userRedPacketService.grepRedPacket(redPacketId, userId);
        boolean flag = ret > 0;
        redPacket.put("success", flag);
        redPacket.put("message", flag ? "抢红包成功" : "抢红包失败");
        return redPacket;
    }


    @RequestMapping(value = "/grapRedPacketForUpdate/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grapRedPacketForUpdate(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> result = new HashMap<>();
        int ret = userRedPacketService.grepRedPacketForUpdate(redPacketId, userId);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "抢红包成功" : "抢红包失败");
        return result;
    }

    @RequestMapping(value = "/grapRedPacketByCAS/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grapRedPacketByCAS(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> result = new HashMap<>();
        int ret = userRedPacketService.grepRedPacketByCAS(redPacketId, userId);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "抢红包成功" : "抢红包失败");
        return result;
    }

    @RequestMapping(value = "/grapRedPacketByCASTime/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grapRedPacketByCASTime(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> result = new HashMap<>();
        int ret = userRedPacketService.grepRedPacketByCASTime(redPacketId, userId);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "抢红包成功" : "抢红包失败");
        return result;
    }

    @RequestMapping(value = "/grapRedPacketByCASNum/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grapRedPacketByCASNum(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> result = new HashMap<>();
        int ret = userRedPacketService.grepRedPacketByCASNum(redPacketId, userId);
        boolean flag = ret > 0;
        result.put("success", flag);
        result.put("message", flag ? "抢红包成功" : "抢红包失败");
        return result;
    }

    @RequestMapping(value = "/grapRedPacket30000")
    @ResponseBody
    public String grapRedPacket30000() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(120000);
        for (int i = 1; i <= 30000; i++) {
            //grepRedPacket(1,i);
            // grepRedPacketForUpdate(2,i);
            grapRedPacketByCAS(3, i);
            // grepRedPacketByCASTime(4,i);
            // grepRedPacketByCASNum(5,i);
        }
        return "success";
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


    @RequestMapping(value = "/grapRedPacketByRedis/{redPacketId}/{userId}")
    @ResponseBody
    public Map<String, Object> grapRedPacketByRedis(@PathVariable("redPacketId") long redPacketId, @PathVariable("userId") long userId) {
        Map<String, Object> resultMap = new HashMap<>();
        long result = userRedPacketService.grepRedPacketByRedis(redPacketId, userId);
        boolean flag = result > 0;
        resultMap.put("result", flag);
        resultMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return resultMap;
    }

}
