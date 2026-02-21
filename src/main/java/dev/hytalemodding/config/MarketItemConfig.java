package dev.hytalemodding.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class MarketItemConfig {

    public static final BuilderCodec<MarketItemConfig> CODEC = BuilderCodec.builder(MarketItemConfig.class, MarketItemConfig::new)
            .append(new KeyedCodec<>("Id", Codec.STRING), (o, v) -> o.id = v, o -> o.id).add()
            .append(new KeyedCodec<>("BasePrice", Codec.DOUBLE), (o, v) -> o.basePrice = v, o -> o.basePrice).add()
            .append(new KeyedCodec<>("TargetStock", Codec.INTEGER), (o, v) -> o.targetStock = v, o -> o.targetStock).add()
            .append(new KeyedCodec<>("Volatility", Codec.INTEGER), (o, v) -> o.volatility = v, o -> o.volatility).add()
            .append(new KeyedCodec<>("Sell_factor", Codec.DOUBLE), (o, v) -> o.sellFactor = v, o -> o.sellFactor).add()
            .append(new KeyedCodec<>("Category", Codec.STRING), (o, v) -> o.category = v, o -> o.category).add()
            .build();

    public MarketItemConfig() {

    }

    public MarketItemConfig(String id, double basePrice, int targetStock, int volatility, double sellFactor, String category) {
        this.id = id;
        this.basePrice = basePrice;
        this.targetStock = targetStock;
        this.volatility = volatility;
        this.sellFactor = sellFactor;
        this.category = category;
    }

    public String id;
    public double basePrice;
    public int targetStock;
    public int volatility;
    public double sellFactor;
    public String category;
}
