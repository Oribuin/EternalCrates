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
    private static boolean enabled = false;

    public PAPI(final EternalCrates plugin) {
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

            final Map<String, Integer> keys = this.data.getUsersVirtualKeys(player.getUniqueId());
            return String.valueOf(keys.getOrDefault(args[1], 0));
        }
        return params;
    }

    /**
     * Apply PAPI placeholders to a string
     *
     * @param player The player to apply the placeholders to
     * @param text   The text to apply the placeholders to
     * @return The text with the placeholders applied
     */
    public static String apply(OfflinePlayer player, String text) {
        if (enabled) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
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

    @Override
    public boolean canRegister() {
        return enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
