package xyz.oribuin.eternalcrates.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.orilibrary.util.FileUtils;

import java.util.*;

public final class PluginUtils {

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

        final String[] fileNames = new String[]{"wheel", "spiral", "quick"};
        Arrays.stream(fileNames).forEach(s -> FileUtils.createFile(plugin, "crates", s + ".yml"));

    }

}
