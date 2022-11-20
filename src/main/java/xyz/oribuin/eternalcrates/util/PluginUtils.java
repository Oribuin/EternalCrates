package xyz.oribuin.eternalcrates.util;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.hook.CustomItemPlugin;
import xyz.oribuin.eternalcrates.hook.PAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PluginUtils {

    private PluginUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Format a string list into a single string.
     *
     * @param list      The strings being converted
     * @param delimiter The delimiter between each string
     * @return the converted string.
     */
    public static String formatList(List<String> list, String delimiter) {
        return String.join(delimiter, list);
    }

    /**
     * Format a location into a readable String.
     *
     * @param loc The location
     * @return The formatted Location.
     */
    public static String formatLocation(Location loc) {
        return loc != null ? loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() : "None";
    }

    /**
     * Get the block location of the location.;
     *
     * @param loc The location;
     * @return The block location
     */
    public static Location getBlockLoc(Location loc) {
        final var location = loc.clone();
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
     * Get a bukkit color from a hex code
     *
     * @param hex The hex code
     * @return The bukkit color
     */
    public static Color fromHex(String hex) {
        if (hex == null)
            return Color.BLACK;

        java.awt.Color awtColor;
        try {
            awtColor = java.awt.Color.decode(hex);
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }

        return Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
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
    public static <T> T get(CommentedFileConfiguration config, String path, T def) {
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
    public static <T> T get(CommentedConfigurationSection section, String path, T def) {
        return section.get(path) != null ? (T) section.get(path) : def;
    }

    /**
     * Get the total number of spare slots in a player's inventory
     *
     * @param player The player
     * @return The amount of empty slots.
     */
    public static int getSpareSlots(Player player) {
        final List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 36; i++)
            slots.add(i);

        return (int) slots.stream().map(integer -> player.getInventory().getItem(integer))
                .filter(itemStack -> itemStack == null || itemStack.getType() == Material.AIR)
                .count();
    }

    /**
     * Gets a location as a string key
     *
     * @param location The location
     * @return the location as a string key
     * @author Esophose
     */
    public static String locationAsKey(Location location) {
        return String.format("%s-%.2f-%.2f-%.2f", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    /**
     * Format an enum name to be more readable.
     *
     * @param enumName The enum name
     * @return The formatted name
     */
    public static String formatEnum(String enumName) {
        return WordUtils.capitalizeFully(enumName.toLowerCase().replace("_", " "));
    }

    /**
     * Get ItemStack from CommentedFileSection path
     *
     * @param config       The CommentedFileSection
     * @param path         The path to the item
     * @param player       The player
     * @param placeholders The placeholders
     * @return The itemstack
     */
    public static ItemStack getItemStack(CommentedConfigurationSection config, String path, Player player, StringPlaceholders placeholders) {
        var pluginItem = config.getString(path + ".plugin");

        if (pluginItem != null) {
            return CustomItemPlugin.parse(pluginItem);
        }

        var material = Material.getMaterial(get(config, path + ".material", "STONE"));
        if (material == null) {
            return new ItemStack(Material.STONE);
        }

        // Format the item lore
        var lore = new ArrayList<String>(get(config, path + ".lore", new ArrayList<>()))
                .stream()
                .map(s -> format(player, s, placeholders))
                .collect(Collectors.toList());

        // Get item flags
        var flags = get(config, path + ".flags", new ArrayList<String>())
                .stream()
                .map(String::toUpperCase)
                .map(ItemFlag::valueOf)
                .toArray(ItemFlag[]::new);

        // Build the item stack
        var builder = new ItemBuilder(material)
                .setName(format(player, get(config, path + ".name", null), placeholders))
                .setLore(lore)
                .setAmount(Math.max(get(config, path + ".amount", 1), 1))
                .setFlags(flags)
                .glow(get(config, path + ".glow", false))
                .setTexture(get(config, path + ".texture", null))
                .setPotionColor(fromHex(get(config, path + ".potion-color", null)))
                .setModel(get(config, path + ".model-data", -1));

        // Get item owner
        var owner = get(config, path + ".owner", null);
        if (owner != null)
            builder.setOwner(Bukkit.getOfflinePlayer(UUID.fromString((String) owner)));

        // Get item enchantments
        final var enchants = config.getConfigurationSection(path + ".enchants");
        if (enchants != null) {
            enchants.getKeys(false).forEach(key -> {
                var enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key.toLowerCase()));
                if (enchantment == null)
                    return;

                builder.addEnchant(enchantment, enchants.getInt(key));
            });
        }

        return builder.create();
    }

    /**
     * Get ItemStack from CommentedFileSection path
     *
     * @param config The CommentedFileSection
     * @param path   The path to the item
     * @return The itemstack
     */
    public static ItemStack getItemStack(CommentedConfigurationSection config, String path) {
        return getItemStack(config, path, null, StringPlaceholders.empty());
    }

    public static ItemStack getItemStack(CommentedConfigurationSection config, String path, Player player) {
        return getItemStack(config, path, player, StringPlaceholders.empty());
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player The player to format the string for
     * @param text   The string to format
     * @return The formatted string
     */
    public static String format(Player player, String text) {
        return format(player, text, StringPlaceholders.empty());
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    public static String format(Player player, String text, StringPlaceholders placeholders) {
        if (text == null)
            return null;

        return HexUtils.colorify(PAPI.apply(player, placeholders.apply(text)));
    }

    /**
     * Create a 3d hollow cube from 2 org.bukkit.Location objects with distance between them
     *
     * @param corner1          The first corner of the cube
     * @param corner2          The second corner of the cube
     * @param particleDistance The distance between particles
     * @return A list of blocks that make up the cube
     */
    public static List<Location> getCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<>();
        var world = corner1.getWorld();
        var minX = Math.min(corner1.getX(), corner2.getX());
        var minY = Math.min(corner1.getY(), corner2.getY());
        var minZ = Math.min(corner1.getZ(), corner2.getZ());
        var maxX = Math.max(corner1.getX(), corner2.getX());
        var maxY = Math.max(corner1.getY(), corner2.getY());
        var maxZ = Math.max(corner1.getZ(), corner2.getZ());
        for (var x = minX; x <= maxX; x += particleDistance) {
            for (var y = minY; y <= maxY; y += particleDistance) {
                for (var z = minZ; z <= maxZ; z += particleDistance) {
                    if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }

        return result;
    }

    /**
     * Parse a list of strings from 1-1 to a stringlist
     *
     * @param list The list to parse
     * @return The parsed list
     */
    public static List<Integer> parseList(List<String> list) {
        var newList = new ArrayList<Integer>();
        for (var s : list) {
            var split = s.split("-");
            if (split.length != 2) {
                continue;
            }

            newList.addAll(getNumberRange(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
        }

        return newList;
    }

    /**
     * Get a range of numbers as a list
     *
     * @param start The start of the range
     * @param end   The end of the range
     * @return A list of numbers
     */
    public static List<Integer> getNumberRange(int start, int end) {
        if (start == end) {
            return List.of(start);
        }

        final var list = new ArrayList<Integer>();
        for (int i = start; i <= end; i++) {
            list.add(i);
        }

        return list;
    }

    public static String getLocationsFormatted(List<Location> locations) {
        return locations.stream().map(PluginUtils::formatLocation).collect(Collectors.joining(", "));
    }

}
