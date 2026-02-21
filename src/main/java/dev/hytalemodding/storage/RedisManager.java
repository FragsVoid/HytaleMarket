package dev.hytalemodding.storage;

import dev.hytalemodding.objects.ItemData;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;

import java.io.InputStream;
import java.util.Map;

public class RedisManager {

    private JedisPooled jedis;
    private final String prefix = "hytalemarket:stock:";

    public RedisManager(String host, int port, String password) {
        if (password != null && !password.isEmpty()) {
            this.jedis = new JedisPooled(host, port, null, password);
        } else {
            this.jedis = new JedisPooled(host, port);
        }
    }

    public void setStock(ItemData data) {
        jedis.set(prefix + data.getId(), String.valueOf(data.getCurrentStock()));
    }

    public void setStock(String id, int currentStock) {
        jedis.set(prefix + id, String.valueOf(currentStock));
    }

    public void modifyStock(String itemId, int amount) {
        jedis.incrBy(prefix + itemId, amount);
    }

    public void close() {
        jedis.close();
    }

    public int getStock(String itemId) {
        String value = jedis.get(prefix + itemId);
        if (value == null || value.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(value);
    }
}
