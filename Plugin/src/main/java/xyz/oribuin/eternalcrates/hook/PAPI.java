package xyz.oribuin.eternalcrates.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;

// oh yes
public class PAPI extends PlaceholderExpansion {

    private final EternalCrates plugin;

    public PAPI(final EternalCrates plugin) {
        this.plugin = plugin;
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
