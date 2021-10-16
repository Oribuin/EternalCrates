package xyz.oribuin.eternalcrates.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.orilibrary.util.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PluginUtils {

    /**
     * Format a string list into a single string.
     *
     * @param stringList The strings being converted
     * @return the converted string.
     */
    public static String formatList(List<String> stringList) {
        final StringBuilder builder = new StringBuilder();
        stringList.forEach(s -> builder.append(s).append("\n"));
        return builder.toString();
    }

    /**
     * Format a location into a readable String.
     *
     * @param loc The location
     * @return The formatted Location.
     */
    public static String formatLocation(Location loc) {
        if (loc == null)
            return "None";

        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    /**
     * Get the block location of the location.;
     *
     * @param loc The location;
     * @return The block location
     */
    public static Location getBlockLoc(Location loc) {
        final Location location = loc.clone();
        return new Location(location.getWorld(), location.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Center a location to the center of the block.
     *
     * @param location The location to be centered.
     * @return The centered location.
     */
    public static Location centerLocation(Location location) {
        final Location loc = location.clone();
        loc.add(0.5, 0.5, 0.5);
        loc.setYaw(180f);
        loc.setPitch(0f);

        return loc;
    }

    /**
     * Create all the default files required for the different crate types
     *
     * @param plugin The plugin.
     */
    public static void createDefaultFiles(final EternalCrates plugin) {
        final List<FileConfiguration> configs = new ArrayList<>();

        final String[] fileNames = new String[]{
                "celebration", "chicken", "csgo", "fountain", "rings", "sparkles", "wheel"
        };
        Arrays.stream(fileNames).forEach(s -> FileUtils.createFile(plugin, "crates", s + ".yml"));
    }

    /**
     * Get a bukkit color from a hex code
     *
     * @param hex The hex code
     * @return The bukkit color
     */
    public static Color fromHex(String hex) {
        if (hex == null)
            return Color.BLACK;

        final java.awt.Color color;
        try {
            color = java.awt.Color.decode(hex);
        } catch (final NumberFormatException ex) {
            return Color.BLACK;
        }

        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Get a configuration value or default from the file config
     *
     * @param config The configuration file.
     * @param path   The path to the value
     * @param def    The default value if the original value doesnt exist
     * @return The config value or default value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(FileConfiguration config, String path, T def) {
        return config.get(path) != null ? (T) config.get(path) : def;
    }

    /**
     * Get a value from a configuration section.
     *
     * @param section The configuration section
     * @param path    The path to the option.
     * @param def     The default value for the option.
     * @return The config option or the default.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(ConfigurationSection section, String path, T def) {
        return section.get(path) != null ? (T) section.get(path) : def;
    }
}
