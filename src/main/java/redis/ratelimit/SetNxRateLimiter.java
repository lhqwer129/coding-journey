package redis.ratelimit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import redis.jedis.JedisLauncher;

import java.util.stream.IntStream;

/**
 * description: setnx rate limiter
 * setnx: set if key not existed
 * formula: setnx key value [EX seconds] [PX milliseconds] [NX|XX]
 *
 * @author lihui
 * @create 2023-08-28 21:00
 **/
public class SetNxRateLimiter {

    /**
     *
     * @param key rate limiter key
     * @param limit max count
     * @param period period,  unit: ms
     * @return true or false
     */
    public static boolean rateLimiter(String key, int limit, long period) {
        Jedis jedis = JedisLauncher.JedisPool.getResource();

        SetParams setParams = new SetParams();
        setParams.nx();
        setParams.px(period);
        String res = jedis.set(key, String.valueOf(limit), setParams);
        if ("OK".equals(res)) {
            return true;
        }

        long count = jedis.decr(key);
        return count > 0;
    }
    
    public static void main(String[] args) {

        IntStream.range(1, 100)
                .boxed()
                .parallel()
                .forEach(i -> {
                    System.out.println(rateLimiter("setnx_limiter", 5,  1000));
                });
    }

}
