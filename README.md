## 简介

本工程是抢红包场景的示例程序，用于帮助自己理解高并发场景的问题，同时给出了一些解决该场景下问题的解决方案，程序中的大部分内容来自于书籍 《Java EE互联网轻量级框架整合开发 SSM框架（Spring MVC+Spring+MyBatis）和Redis实现》 第 22 章，只是工程的结构进行了重新构建，另外对于其中代码的部分逻辑进行了修正，用于模拟并发的示例程序也进行了重新设计。

## 测试方式

1. 新建数据库表，sql 文件位于 db 文件夹下。

2. 修改 spring-boot 配置文件的 url、username、password 为当前环境的值。

3. 启动服务端程序 top.aprilyolies.redPacket.RedPacketApplication。

4. 执行 top.aprilyolies.redPacket.RedPacketTest 文件中的各项测试程序。

说明：在测试基于 redis 的抢红包程序时，需要启动本地的 redis 服务端，同时在其中写入红包信息，指令如下。

> hset red_packet_1 stock 2000

## 测试 api 说明

* top.aprilyolies.redPacket.RedPacketTest#grepRedPacket 一次抢红包请求

* top.aprilyolies.redPacket.RedPacketTest#multiThreadGrepRedPacket 存在红包超发的抢红包场景

* top.aprilyolies.redPacket.RedPacketTest#multiThreadGrepRedPacketForUpdate 通过悲观锁解决并发场景下红包超发问题

* top.aprilyolies.redPacket.RedPacketTest#multiThreadGrepRedPacketByCAS 通过乐观锁解决并发场景下红包超发问题，同时又产生了抢红包过程失败率高的问题

* top.aprilyolies.redPacket.RedPacketTest#multiThreadGrepRedPacketByCASTime 通过乐观锁解决并发场景下红包超发问题，使用限时操作保证抢红包的成功率

* top.aprilyolies.redPacket.RedPacketTest#multiThreadGrepRedPacketByCASNum 通过乐观锁解决并发场景下红包超发问题，使用限次操作保证抢红包的成功率

* top.aprilyolies.redPacket.RedPacketTest#multiThreadGrepRedPacketByRedis 通过 redis 的 lua 脚本方式来解决并发场景下的红包超发问题