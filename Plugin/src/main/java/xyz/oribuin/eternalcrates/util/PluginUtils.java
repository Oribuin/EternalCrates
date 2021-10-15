package xyz.oribuin.eternalcrates.util;

import org.bukkit.Color;
import org.bukkit.Location;
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

        final String[] fileNames = new String[]{"csgo", "rings", "sparkles", "wheel"};
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

}
