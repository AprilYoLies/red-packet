package top.aprilyolies.redPacket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("top.aprilyolies.redPacket.mapper")
@EnableAsync //开启异步调用
public class RedPacketApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedPacketApplication.class, args);
    }
}


