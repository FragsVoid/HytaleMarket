package dev.hytalemodding.objects;

import dev.hytalemodding.storage.RedisManager;

import java.util.concurrent.CompletableFuture;

public class ItemData {

    private final String id;
    private final String category;
    private final double basePrice;
    private final long targetStock;
    private final double volatility;
    private final double sellFactor;

    private long currentStock;

    private final RedisManager redisManager;

    public ItemData(String id, String category, double basePrice, long targetStock, long currentStock, double volatility, double sellFactor, RedisManager redisManager) {
        this.id = id;
        this.category = category;
        this.basePrice = basePrice;
        this.targetStock = targetStock;
        this.currentStock = currentStock;
        this.volatility = volatility / 100;
        this.sellFactor = sellFactor;
        this.redisManager = redisManager;
    }

    public double getBuyPrice() {
        return calculatePriceAtStock(currentStock);
    }

    private double calculatePriceAtStock(long stock) {
        long safeStock = Math.max(1, stock);
        double ratio = (double) targetStock / safeStock;
        double multiplier = Math.pow(ratio, volatility);
        return basePrice * multiplier;
    }

    public double getSellPrice() {
        return getBuyPrice() * sellFactor;
    }

    private double getSellPriceAtStock(long stock) {
        return calculatePriceAtStock(stock) * sellFactor;
    }

    public double calculateTotalBuyPrice(int amount) {
        double totalAmount = 0;
        long tempStock = this.currentStock;
        for (int i = 0; i < amount; i++) {
            totalAmount += calculatePriceAtStock(tempStock);
            tempStock--;
        }
        return totalAmount;
    }

    public double calculateTotalSellPrice(int amount) {
        double totalAmount = 0;
        long tempStock = this.currentStock;
        for (int i = 0; i < amount; i++) {
            totalAmount += getSellPriceAtStock(tempStock);
            tempStock++;
        }
        return totalAmount;
    }

    public void onPlayerSell(int amount) {
        addCurrentStock(amount);
    }

    public void onPlayerBuy(int amount) {
        removeCurrentStock(amount);
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
        if (redisManager != null) {
            redisManager.setStock(this.id, currentStock);
        }
    }

    public void removeCurrentStock(int quantity) {
        this.currentStock -= quantity;
        updateRedis(-quantity);
    }

    public void addCurrentStock(int quantity) {
        this.currentStock += quantity;
        updateRedis(quantity);
    }

    private void updateRedis(int delta) {
        if (redisManager != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisManager.modifyStock(this.id, delta);
                } catch (Exception e) {
                    System.out.println("Error saving into Redis");
                }
            });
        }
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public double getBasePrice() { return basePrice; }
    public long getTargetStock() { return targetStock; }
    public long getCurrentStock() { return currentStock; }
    public double getVolatility() { return volatility; }
}