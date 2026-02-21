package dev.hytalemodding.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.simple.IntegerCodec;
import com.hypixel.hytale.codec.codecs.simple.StringCodec;
import dev.hytalemodding.objects.RedisConfig;

public class MarketConfig {

    private MarketItemConfig[] items;
    // Nuevos campos para Redis
    private String redisHost;
    private int redisPort;
    private String redisPassword;

    public static final BuilderCodec<MarketConfig> CODEC =
            BuilderCodec.builder(MarketConfig.class, MarketConfig::new)
                    .append(new KeyedCodec<>("Items", new ArrayCodec<>(MarketItemConfig.CODEC, MarketItemConfig[]::new)),
                            (config, v) -> config.items = v,
                            (config) -> config.items).add()
                    .append(new KeyedCodec<>("RedisHost", new StringCodec()),
                            (config, v) -> config.redisHost = v,
                            (config) -> config.redisHost).add()
                    .append(new KeyedCodec<>("RedisPort", new IntegerCodec()),
                            (config, v) -> config.redisPort = v,
                            (config) -> config.redisPort).add()
                    .append(new KeyedCodec<>("RedisPassword", new StringCodec()),
                            (config, v) -> config.redisPassword = v,
                            (config) -> config.redisPassword)
                    .add()
                    .build();

    public MarketConfig() {
        this.redisHost = "127.0.0.1";
        this.redisPort = 6379;
        this.redisPassword = "";

        this.items = new MarketItemConfig[] {
                new MarketItemConfig("Plant_Crop_Potato_Item", 4.5, 700000, 50, 0.6, "farming"),
                new MarketItemConfig("Plant_Crop_Carrot_Item", 4.5, 700000, 50, 0.6, "farming"),
                new MarketItemConfig("Rock_Gem_Diamond", 500.0, 7500, 60, 0.8, "mining"),
                new MarketItemConfig("Ingredient_Bar_Iron", 8.0, 750000, 85, 0.4, "mining"),
                new MarketItemConfig("Wood_Ash_Trunk", 16.0, 1000000, 75, 0.4, "foraging")
        };
    }

    public MarketItemConfig[] getItems() { return items; }
    public String getRedisHost() { return redisHost; }
    public int getRedisPort() { return redisPort; }
    public String getRedisPassword() { return redisPassword; }
}
