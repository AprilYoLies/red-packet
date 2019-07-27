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

    private final int people = 2500;

    private CountDownLatch latch = new CountDownLatch(people);


    @Before
    public void init() {
        template = new RestTemplate();
    }

    @Test
    public void grepRedPacket() {
        String url = "http://localhost:8080/user/grepRedPacket/1/1";
        String packet = template.getForObject(url, String.class);
        System.out.println(packet);
    }

    @Test
    public void multiThreadGrepRedPacket() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        final String url = "http://localhost:8080/user/grepRedPacket/1/1";
        for (int i = 0; i < people; i++) {
            executor.submit(new GrepRedPacketTask(url));
        }
        latch.await();
    }

    @Test
    public void multiThreadGrepRedPacketForUpdate() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        final String url = "http://localhost:8080/user/grepRedPacketForUpdate/1/1";
        for (int i = 0; i < people; i++) {
            executor.submit(new GrepRedPacketTask(url));
        }
        latch.await();
    }

    @Test
    public void multiThreadGrepRedPacketByCAS() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        final String url = "http://localhost:8080/user/grepRedPacketByCAS/1/1";
        for (int i = 0; i < people; i++) {
            executor.submit(new GrepRedPacketTask(url));
        }
        latch.await();
    }

    @Test
    public void multiThreadGrepRedPacketByCASTime() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        final String url = "http://localhost:8080/user/grepRedPacketByCASTime/1/1";
        for (int i = 0; i < people; i++) {
            executor.submit(new GrepRedPacketTask(url));
        }
        latch.await();
    }

    @Test
    public void multiThreadGrepRedPacketByCASNum() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        final String url = "http://localhost:8080/user/grepRedPacketByCASNum/1/1";
        for (int i = 0; i < people; i++) {
            executor.submit(new GrepRedPacketTask(url));
        }
        latch.await();
    }

    @Test
    public void multiThreadGrepRedPacketByRedis() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        final String url = "http://localhost:8080/user/grepRedPacketByRedis/1/1";
        for (int i = 0; i < people; i++) {
            executor.submit(new GrepRedPacketTask(url));
        }
        latch.await();
    }

    private class GrepRedPacketTask implements Runnable {
        private final String url;

        public GrepRedPacketTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            template.getForObject(url, String.class);
            latch.countDown();
        }
    }
}
