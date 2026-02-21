package dev.hytalemodding.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.hytalemodding.HytaleMarket;
import dev.hytalemodding.MarketUI;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ShopCommand extends AbstractPlayerCommand {

    private final HytaleMarket plugin;

    public ShopCommand(String name, String description, HytaleMarket plugin) {
        super(name, description);
        this.plugin = plugin;
        this.setPermissionGroup(GameMode.Adventure);
    }


    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        openGui(player, ref);
    }

    public void openGui(Player player, Ref<EntityStore> ref) {
        Store<EntityStore> store = ref.getStore();

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        World world = player.getWorld();

        CompletableFuture.runAsync(() -> {
            try {
                CustomUIPage page;

                page = new MarketUI(playerRef, CustomPageLifetime.CanDismiss, plugin);
                player.getPageManager().openCustomPage(ref, store, page);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, world);
    }
}
