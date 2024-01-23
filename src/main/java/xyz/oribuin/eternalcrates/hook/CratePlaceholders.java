package xyz.oribuin.eternalcrates.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.DataManager;

import java.util.Map;

public class CratePlaceholders extends PlaceholderExpansion {

    private final EternalCrates plugin;
    private final DataManager data;
    private static boolean enabled = false;

    public CratePlaceholders(final EternalCrates plugin) {
        this.plugin = plugin;
        this.data = plugin.getManager(DataManager.class);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            enabled = true;
        }
    }

    // todo
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (params.toLowerCase().startsWith("keys_")) {
            String[] args = params.split("_");
            if (args.length < 2)
                return "Unknown Crate";

            final Map<String, Integer> keys = this.data.user(player.getUniqueId()).getKeys();
            return String.valueOf(keys.getOrDefault(args[1], 0));
        }
        return params;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "eternalcrates";
    }

    @Override
    public @NotNull String getAuthor() {
        return "oribuin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public static boolean isEnabled() {
        return enabled;
    }

}
