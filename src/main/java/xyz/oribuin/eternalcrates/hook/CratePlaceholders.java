package xyz.oribuin.eternalcrates.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.DataManager;

public class CratePlaceholders extends PlaceholderExpansion {

    private static boolean enabled = false;
    private final EternalCrates plugin;
    private final DataManager data;

    public CratePlaceholders(final EternalCrates plugin) {
        this.plugin = plugin;
        this.data = plugin.getManager(DataManager.class);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            enabled = true;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!params.toLowerCase().startsWith("keys")) return null;
        String[] args = params.split("_");
        if (args.length < 2)
            return "Unknown Crate";

        return this.data.user(player.getUniqueId())
                .thenApply(keys -> {
                    if (keys == null)
                        return String.valueOf(0);

                    return String.valueOf(keys.getContent().getOrDefault(args[1], 0));
                })
                .getNow(String.valueOf(0));
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

}
