package redis.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

/**
 * description: Jedis is a client library inside Redis. that is lightweight and ease of use.
 *
 * @author lihui
 * @create 2023-08-16 18:41
 **/
public class JedisLauncher {

    private static final String JEDIS_CONNECTION_HOST = "127.0.0.1";
    private static final Integer JEDIS_CONNECTION_PORT = 6379;

    public static final Jedis SingleJedis  = new Jedis(JEDIS_CONNECTION_HOST, JEDIS_CONNECTION_PORT);

    public static final JedisPool JedisPool = new JedisPool(JEDIS_CONNECTION_HOST, JEDIS_CONNECTION_PORT);

    static {
        // jedisPool set core
        JedisPool.setMaxTotal(30);
    }

    public static void main(String[] args) throws InterruptedException {
        // 1. create jedis instance
        Jedis jedis = new Jedis(JEDIS_CONNECTION_HOST, JEDIS_CONNECTION_PORT);

        // 2. Set Params Builder
        SetParams setParams = new SetParams();
        setParams.nx();
        setParams.ex(10);

        // 3. set key-value
        jedis.set("foo", "bar", setParams);
        System.out.println(jedis.get("foo"));

        // 4. wait for 10 seconds
        Thread.sleep(10000);
        if (jedis.get("foo") == null) {
            System.out.println("foo is expired");
        }

        // 5. close jedis
        jedis.close();

    }

}
