package redis.ratelimit;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.jedis.JedisLauncher;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * description: IncrRateLimiter
 *
 * @author lihui
 * @create 2023-08-17 20:58
 **/
public class IncrRateLimiter {

    /**
     * Use the redis incr command to implement a rate limiter. that is, the number of requests per unit time is limited.
     *
     * not thread safety， because the incr and expire commands is not atomic. so, wo can use lua script to implement.
     */
    public boolean limiter(Jedis jedis, String key, int count, TimeUnit timeUnit, int period) {
        long counter = jedis.incr(key);
        if (counter == 1) {
            jedis.expire(key, (int) timeUnit.toSeconds(period));
        }

        return counter <= count;

    }

    public static boolean luaLimiter(Jedis jedis, String key, int count, TimeUnit timeUnit, int period) {
        String luaScript = "local counter = redis.call('incr', KEYS[1])\n" +
                "if tonumber(counter) == 1 then\n" +
                "    redis.call('expire', KEYS[1], ARGV[1])\n" +
                "end\n" +
                "return counter";
        Object result = jedis.eval(luaScript, Collections.singletonList(key), Collections.singletonList(String.valueOf(timeUnit.toSeconds(period))));
        return Long.parseLong(result.toString()) <= count;
    }

    @Test
    public static void luaLimiterTester(String[] args) {
        CountDownLatch gate = new CountDownLatch(1);
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                // jedis instance is not thread safety, so we can use jedis pool to get jedis instance.
                Jedis jedis = JedisLauncher.JedisPool.getResource();
                try {
                    gate.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                boolean res = luaLimiter(jedis, "luaLimiter", 5, TimeUnit.SECONDS, 1);
                System.out.printf("current thread[%s] request result is [%s]%n", Thread.currentThread().getName(), res);
            }).start();
        }

        gate.countDown();
        try {
            // hung up the main thread
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JedisLauncher.JedisPool.close();
    }
}
