package dev.hytalemodding.config;

import dev.hytalemodding.HytaleMarket;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessagesManager {

    private final HytaleMarket plugin;
    private final File messagesFile;
    private FileConfiguration file;
    private final Map<String, String> messageCache;

    public MessagesManager(HytaleMarket plugin) {
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataDirectory().toFile(), "messages.yml");
        this.messageCache = new HashMap<>();

        loadMessages();
    }

    public void loadMessages() {
        if (!messagesFile.exists()) {
            try {
                if (messagesFile.createNewFile()) {
                    plugin.getLogger().atInfo().log("Created messages.yml");
                }
            } catch (IOException e) {
                plugin.getLogger().atSevere().log("Failed to create messages.yml", e);
            }
        }

        file = YamlConfiguration.loadConfiguration(messagesFile);
        setDefaults();
        saveFile();
        cacheMessages();
    }

    private void setDefaults() {
        if (!file.contains("no_money")) {
            file.set("no_money", "You do not have enough money");
        }

        if (!file.contains("no_item")) {
            file.set("no_item", "There is no such item!");
        }

        if (!file.contains("bought_item")) {
            file.set("bought_item", "You just bought <item> for <price>");
        }

        if (!file.contains("sold_item")) {
            file.set("sold_item", "You just sold <item> for <price>");
        }
    }

    private void cacheMessages() {
        messageCache.clear();
        for (String key : file.getKeys(true)) {
            if (file.isString(key)) {
                messageCache.put(key, file.getString(key));
            }
        }
    }

    public void saveFile() {
        try {
            file.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().atSevere().log("Failed to save messages.yml", e);
        }
    }

    public String getMessage(String path) {
        return messageCache.getOrDefault(path, "Message not found: " + path);
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);

        for (int i = 0; i < replacements.length - 1; i += 2) {
            String placeholder = replacements[i];
            String value = replacements[i + 1];
            message = message.replace("<" + placeholder + ">", value);
        }

        return message;
    }


    public void reload() {
        loadMessages();
    }
}
