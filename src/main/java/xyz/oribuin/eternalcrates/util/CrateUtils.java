package xyz.oribuin.eternalcrates.util;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.PAPI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CrateUtils {

    private CrateUtils() {
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
        if (loc == null)
            return "None";

        return String.format("%s, %s, %s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Get the block location of the location.;
     *
     * @param loc The location;
     * @return The block location
     */
    public static Location getBlockLoc(Location loc) {
        if (NMSUtil.isPaper())
            return loc.clone().toBlockLocation();

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
        if (NMSUtil.isPaper())
            return location.clone().toCenterLocation();

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

    @Nullable
    public static ItemStack getItemStack(@NotNull CommentedConfigurationSection config, @NotNull String path, @Nullable Player player, @Nullable StringPlaceholders placeholders) {

        Material material = Material.getMaterial(PlaceholderAPI.setPlaceholders(player, config.getString(path + ".material", "")));
        if (material == null)
            return null;

        if (placeholders == null)
            placeholders = StringPlaceholders.empty();

        // Format the item lore
        StringPlaceholders finalPlaceholders = placeholders;
        List<String> lore = new ArrayList<>(config.getStringList(path + ".lore"))
                .stream()
                .map(s -> format(player, s, finalPlaceholders))
                .toList();

        // Get item flags
        ItemFlag[] flags = config.getStringList(path + ".flags")
                .stream()
                .map(String::toUpperCase)
                .map(ItemFlag::valueOf)
                .toArray(ItemFlag[]::new);

        // Build the item stack
        ItemBuilder builder = new ItemBuilder(material)
                .setName(format(player, config.getString(path + ".name"), placeholders))
                .setLore(lore)
                .setAmount(config.getInt(path + ".amount", 1))
                .setFlags(flags)
                .setTexture(config.getString(path + ".texture"))
                .glow(Boolean.parseBoolean(format(player, config.getString(path + ".glow", "false"), placeholders)))
                .setPotionColor(fromHex(config.getString(path + ".potion-color", null)))
                .setModel(parseInteger(format(player, config.getString(path + ".model-data", "-1"), placeholders)));

        // Get item owner
        String owner = config.getString(path + ".owner", null);
        if (owner != null) {
            if (owner.equalsIgnoreCase("self")) {
                builder.setOwner(player);
            } else {
                if (NMSUtil.isPaper() && Bukkit.getOfflinePlayerIfCached(owner) != null)
                    builder.setOwner(Bukkit.getOfflinePlayerIfCached(owner));
                else
                    builder.setOwner(Bukkit.getOfflinePlayer(owner));
            }
        }

        CommentedConfigurationSection enchants = config.getConfigurationSection(path + ".enchants");
        if (enchants != null) {
            enchants.getKeys(false).forEach(key -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key.toLowerCase()));
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
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());
        for (double x = minX; x <= maxX; x += particleDistance) {
            for (double y = minY; y <= maxY; y += particleDistance) {
                for (double z = minZ; z <= maxZ; z += particleDistance) {
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
    @SuppressWarnings("unchecked")
    public static List<Integer> parseList(List<String> list) {
        List<Integer> newList = new ArrayList<>();
        for (String s : list) {
            String[] split = s.split("-");
            if (split.length != 2) continue;

            newList.addAll(getNumberRange(parseInteger(split[0]), parseInteger(split[1])));
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

        final List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add(i);
        }

        return list;
    }

    /**
     * Parse an integer from an object safely
     *
     * @param object The object
     * @return The integer
     */
    private static int parseInteger(Object object) {
        try {
            if (object instanceof Integer)
                return (int) object;

            return Integer.parseInt(object.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public static String getLocationsFormatted(List<Location> locations) {
        return locations.stream().map(CrateUtils::formatLocation).collect(Collectors.joining(", "));
    }

    /**
     * Get an enum from a string value
     *
     * @param enumClass The enum class
     * @param name      The name of the enum
     * @param <T>       The enum type
     * @return The enum
     */
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name) {
        if (name == null)
            return null;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return null;
    }

    /**
     * Create a file from the plugin's resources
     *
     * @param rosePlugin The plugin
     * @param fileName   The file name
     * @return The file
     */
    @NotNull
    public static File createFile(@NotNull RosePlugin rosePlugin, @NotNull String fileName) {
        File file = new File(rosePlugin.getDataFolder(), fileName); // Create the file

        if (file.exists())
            return file;

        try (InputStream inStream = rosePlugin.getResource(fileName)) {
            if (inStream == null) {
                file.createNewFile();
                return file;
            }

            Files.copy(inStream, Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * Create a file in a folder from the plugin's resources
     *
     * @param rosePlugin The plugin
     * @param folderName The folder name
     * @param fileName   The file name
     * @return The file
     */
    @NotNull
    public static File createFile(@NotNull RosePlugin rosePlugin, @NotNull String folderName, @NotNull String fileName) {
        File folder = new File(rosePlugin.getDataFolder(), folderName); // Create the folder
        File file = new File(folder, fileName); // Create the file
        if (!folder.exists())
            folder.mkdirs();

        if (file.exists())
            return file;

        try (InputStream stream = rosePlugin.getResource(folderName + "/" + fileName)) {
            if (stream == null) {
                file.createNewFile();
                return file;
            }

            Files.copy(stream, Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

}
