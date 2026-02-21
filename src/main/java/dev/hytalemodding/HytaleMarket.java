package dev.hytalemodding;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.hytalemodding.commands.ReloadCommand;
import dev.hytalemodding.commands.ShopCommand;
import dev.hytalemodding.config.MarketConfig;
import dev.hytalemodding.config.MarketItemConfig;
import dev.hytalemodding.config.MessagesManager;
import dev.hytalemodding.objects.ItemData;
import dev.hytalemodding.storage.RedisManager;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HytaleMarket extends JavaPlugin {

    private final Config<MarketConfig> config;
    private RedisManager redisManager;
    private Map<String, ItemData> itemDataList;

    private MessagesManager messagesManager;

    public HytaleMarket(@Nonnull JavaPluginInit init) {
        super(init);

        this.config = this.withConfig("HytaleMarket", MarketConfig.CODEC);
        config.load();
        config.save();

        itemDataList = new HashMap<>();

        try {
            String host = config.get().getRedisHost();
            int port = config.get().getRedisPort();
            String password = config.get().getRedisPassword();
            this.redisManager = new RedisManager(host, port, password);

            this.redisManager.getStock("test_connection");

            System.out.println("[HytaleMarket] Connected to Redis");
        } catch (Exception ex) {
            System.out.println("[HytaleMarket] RedisManager initialization failed.");
            this.redisManager = null;
        }
        loadItems();
    }


    @Override
    protected void setup() {
        messagesManager = new MessagesManager(this);

        this.getCommandRegistry().registerCommand(new ShopCommand("shop", "Opens a virtual shop", this));
        this.getCommandRegistry().registerCommand(new ReloadCommand(this));
    }

    public Config<MarketConfig> getConfig() {
        return config;
    }

    //Common.UI.Custom
    public void loadItems() {
        itemDataList.clear();
        for (MarketItemConfig item : config.get().getItems()) {

            long initialStock = item.targetStock;

            if (redisManager != null) {
                int savedStock = redisManager.getStock(item.id);

                if (savedStock != -1) {
                    initialStock = savedStock;
                } else {
                    redisManager.setStock(item.id, (int) initialStock);
                }
            }

            ItemData data = new ItemData(
                    item.id,
                    item.category,
                    item.basePrice,
                    item.targetStock,
                    initialStock,
                    item.volatility,
                    item.sellFactor,
                    redisManager
            );

            itemDataList.put(item.id, data);
        }
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public ItemData getItemData(String id) {
        return itemDataList.get(id);
    }

    public List<ItemData> getItems() {
        return itemDataList.values().stream().toList();
    }
}