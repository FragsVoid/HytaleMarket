package dev.hytalemodding.commands;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.hytalemodding.HytaleMarket;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ReloadCommand extends CommandBase {

    private final HytaleMarket plugin;

    public ReloadCommand(HytaleMarket plugin) {
        super("shopreload", "Reloads the config");
        this.plugin = plugin;
        this.setPermissionGroup(GameMode.Creative);
    }

    @Override
    protected void executeSync(@NotNull CommandContext commandContext) {
        try {
            plugin.getMessagesManager().reload();
            plugin.getConfig().load();
            plugin.getConfig().save();
            plugin.loadItems();
            commandContext.sendMessage(Message.raw("Configuration files have been reloaded!").color(Color.GREEN));
        } catch (Exception ex) {
            commandContext.sendMessage(Message.raw("An error ocurred while reloading configuration files!").color(Color.RED));
            ex.printStackTrace();
        }
    }
}
