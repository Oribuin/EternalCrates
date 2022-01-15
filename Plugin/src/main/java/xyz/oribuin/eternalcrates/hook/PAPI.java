package xyz.oribuin.eternalcrates.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;

import java.util.Map;

// oh yes
public class PAPI extends PlaceholderExpansion {

    private final EternalCrates plugin;
    private final DataManager data;
    private final CrateManager crateManager;

    public PAPI(final EternalCrates plugin) {
        this.plugin = plugin;
        this.data = this.plugin.getManager(DataManager.class);
        this.crateManager = this.plugin.getManager(CrateManager.class);

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
        return null;
    }

    @Override
    public @NotNull String getAuthor() {
        return null;
    }

    @Override
    public @NotNull String getVersion() {
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

}
