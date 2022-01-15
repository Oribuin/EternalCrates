package xyz.oribuin.eternalcrates.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.DataManager;

import java.util.Map;

// oh yes
public class PAPI extends PlaceholderExpansion {

    private final EternalCrates plugin;
    private final DataManager data;

    public PAPI(final EternalCrates plugin) {
        this.plugin = plugin;
        this.data = plugin.getManager(DataManager.class);
        this.register();
    }

    /**
     * Parse a message through PlaceholderAPI
     *
     * @param player  The player the messages are applied to
     * @param message the message being parsed
     * @return the parsed message
     */
    public static String apply(OfflinePlayer player, String message) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            return message;

        return PlaceholderAPI.setPlaceholders(player, message);
    }

    // todo
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (params.toLowerCase().startsWith("keys_")) {
            String[] args = params.split("_");
            if (args.length < 2)
                return "Unknown Crate";

            final Map<String, Integer> keys = this.data.getCachedVirtual().get(player.getUniqueId());
            return String.valueOf(keys.getOrDefault(args[1], 0));
        }
        return params;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

}
