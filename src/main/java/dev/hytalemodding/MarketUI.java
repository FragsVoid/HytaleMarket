package dev.hytalemodding;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.entity.entities.player.pages.RespawnPage;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.hytalemodding.objects.ItemData;
import dev.hytalemodding.utils.NumberUtils;
import xyz.herberto.foxEconomy.api.FoxAPI;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;
import java.util.UUID;

public class MarketUI extends InteractiveCustomUIPage<MarketUI.Data> {

    private final HytaleMarket plugin;

    public MarketUI(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime, HytaleMarket plugin) {
        super(playerRef, lifetime, Data.CODEC);
        this.plugin = plugin;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder builder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        builder.append("uitest.ui");
        builder.set("#Title.TextSpans", Message.raw("Mercado"));

        builder.clear("#ShopContainer");

        List<ItemData> items = plugin.getItems();

        String uiResource = "shop_button.ui";

        for (int i = 0; i < items.size(); i++) {
            ItemData item = items.get(i);

            String selector = "#ShopContainer[" + i + "]";

            builder.append("#ShopContainer", uiResource);
            builder.set(selector + " #Icon.ItemId", item.getId());

            builder.set(selector + " #QuantityLabel.Text", "$" + NumberUtils.formatNumber(item.getBuyPrice()));

            String actionValue = "buy_" + i;

            uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    selector + " #MainButton",
                    EventData.of("Action", actionValue),
                    false
            );

            uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.RightClicking,
                    selector + " #MainButton",
                    EventData.of("ActionR", actionValue),
                    false
            );
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull Data data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null && data.action.startsWith("buy_")) {
            try {
                int index = Integer.parseInt(data.action.substring(4));
                handleBuy(ref, store, index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (data.actionR != null && data.actionR.startsWith("buy_")) {
            try {
                int index = Integer.parseInt(data.actionR.substring(4));
                handleSell(ref, store, index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sendUpdate();
    }

    private void handleBuy(Ref<EntityStore> ref, Store<EntityStore> store, int index) {
        List<ItemData> items = plugin.getItems();
        if (index >= 0 && index < items.size()) {
            ItemData item = items.get(index);
            Player player = store.getComponent(ref, Player.getComponentType());



            FoxAPI api = FoxAPI.getInstance();

            UUID uuid = store.getComponent(ref, UUIDComponent.getComponentType()).getUuid();
            double balance = api.getBalance(uuid);
            if (balance >= item.getBuyPrice()) {
                ItemStack itemStack = new ItemStack(item.getId());
                player.getInventory().getStorage().addItemStack(itemStack);
                api.removeMoney(uuid, item.getBuyPrice());
                player.sendMessage(Message.raw(plugin.getMessagesManager().getMessage("bought_item", "item", item.getId(), "price", NumberUtils.formatNumber(item.getBuyPrice()))));
                item.onPlayerBuy(1);
            } else {
                player.sendMessage(Message.raw(plugin.getMessagesManager().getMessage("no_money")));
            }
        }
    }

    private void handleSell(Ref<EntityStore> ref, Store<EntityStore> store, int index) {
        List<ItemData> items = plugin.getItems();
        if (index >= 0 && index < items.size()) {
            ItemData item = items.get(index);
            Player player = store.getComponent(ref, Player.getComponentType());

            ItemStack itemStack = new ItemStack(item.getId());
            Inventory inventory = player.getInventory();

            if (inventory.getStorage().canRemoveItemStack(itemStack)) {
                inventory.getStorage().removeItemStack(itemStack);
                UUID uuid = store.getComponent(ref, UUIDComponent.getComponentType()).getUuid();
                FoxAPI.getInstance().addMoney(uuid, item.getBuyPrice());
                player.sendMessage(Message.raw(plugin.getMessagesManager().getMessage("sold_item", "item", item.getId(), "price", NumberUtils.formatNumber(item.getSellPrice()))));
                item.onPlayerSell(1);
            } else {
                player.sendMessage(Message.raw(plugin.getMessagesManager().getMessage("no_item")));
            }
        }
    }

    public static class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (data, v) -> data.action = v, data -> data.action).add()
                .append(new KeyedCodec<>("ActionR", Codec.STRING), (data, v) -> data.actionR = v, data -> data.actionR).add()
                .build();

        private String action;
        private String actionR;
    }
}

