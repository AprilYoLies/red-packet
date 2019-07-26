package top.aprilyolies.redPacket;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author EvaJohnson
 * @Date 2019-07-26
 * @Email g863821569@gmail.com
 */
public class RedPacketTest {
    private RestTemplate template;

    private final int people = 30000;

    private CountDownLatch latch = new CountDownLatch(people);


    @Before
    public void init() {
        template = new RestTemplate();
    }

    @Test
    public void grepRedPacket() {
        String url = "http://localhost:8080/user/grapRedPacket/1/1";
        String packet = template.getForObject(url, String.class);
        System.out.println(packet);
    }

    @Test
    public void multiThreadGrepRedPacket() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        for (int i = 0; i < people; i++) {
            executor.submit(new GrepRedPacketTask());
        }
        latch.await();
    }

    private class GrepRedPacketTask implements Runnable {
        private static final String url = "http://localhost:8080/user/grapRedPacket/1/1";

        @Override
        public void run() {
            template.getForObject(url, String.class);
            latch.countDown();
        }
    }
}
