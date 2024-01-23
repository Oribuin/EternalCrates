package xyz.oribuin.eternalcrates.util;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.particle.ParticleData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CrateUtils {

    private CrateUtils() {
        throw new IllegalStateException("Utility class");
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
    public static Location center(Location location) {
        if (NMSUtil.isPaper())
            return location.clone().toCenterLocation();

        final Location loc = location.clone();
        loc.add(0.5, 0.5, 0.5);
        loc.setYaw(180f);
        loc.setPitch(0f);

        return loc;
    }

    /**
     * Get the total number of spare slots in a player's inventory
     *
     * @param player The player
     * @return The amount of empty slots.
     */
    public static int getSpareSlots(Player player) {
        return (int) Arrays.stream(player.getInventory().getContents())
                .filter(itemStack -> itemStack == null || itemStack.getType() == Material.AIR)
                .count();
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
     * Get a bukkit color from a hex code
     *
     * @param hex The hex code
     * @return The bukkit color
     */
    public static Color fromHex(String hex) {
        if (hex == null) return Color.BLACK;

        try {
            java.awt.Color decoded = java.awt.Color.decode(hex);
            return Color.fromRGB(decoded.getRed(), decoded.getGreen(), decoded.getBlue());
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }

    }


    /**
     * Deserialize an ItemStack from a CommentedConfigurationSection with placeholders
     *
     * @param section      The section to deserialize from
     * @param sender       The CommandSender to apply placeholders from
     * @param key          The key to deserialize from
     * @param placeholders The placeholders to apply
     * @return The deserialized ItemStack
     */
    @Nullable
    public static ItemStack deserialize(
            @NotNull CommentedConfigurationSection section,
            @Nullable CommandSender sender,
            @NotNull String key,
            @NotNull StringPlaceholders placeholders
    ) {
        final LocaleManager locale = EternalCrates.get().getManager(LocaleManager.class);
        final Material material = Material.getMaterial(locale.format(sender, section.getString(key + ".material"), placeholders), false);
        if (material == null) return null;

        // Load enchantments
        final Map<Enchantment, Integer> enchantments = new HashMap<>();
        final ConfigurationSection enchantmentSection = section.getConfigurationSection(key + ".enchantments");
        if (enchantmentSection != null) {
            for (String enchantmentKey : enchantmentSection.getKeys(false)) {
                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentKey.toLowerCase()));
                if (enchantment == null) continue;

                enchantments.put(enchantment, enchantmentSection.getInt(enchantmentKey, 1));
            }
        }

        // Load potion item flags
        final ItemFlag[] flags = section.getStringList(key + ".flags").stream()
                .map(ItemFlag::valueOf)
                .toArray(ItemFlag[]::new);

        // Load offline player texture
        final String owner = section.getString(key + ".owner");
        OfflinePlayer offlinePlayer = null;
        if (owner != null) {
            if (owner.equalsIgnoreCase("self") && sender instanceof Player player) {
                offlinePlayer = player;
            } else {
                offlinePlayer = NMSUtil.isPaper()
                        ? Bukkit.getOfflinePlayerIfCached(owner)
                        : Bukkit.getOfflinePlayer(owner);
            }
        }

        return new ItemBuilder(material)
                .name(locale.format(sender, section.getString(key + ".name"), placeholders))
                .amount(Math.min(1, section.getInt(key + ".amount", 1)))
                .lore(locale.format(sender, section.getStringList(key + ".lore"), placeholders))
                .flags(flags)
                .glow(section.getBoolean(key + ".glow", false))
                .unbreakable(section.getBoolean(key + ".unbreakable", false))
                .model(toInt(locale.format(sender, section.getString(key + ".model-data", "0"), placeholders)))
                .enchant(enchantments)
                .texture(locale.format(sender, section.getString(key + ".texture"), placeholders))
                .color(fromHex(locale.format(sender, section.getString(key + ".potion-color"), placeholders)))
                .owner(offlinePlayer)
                .build();
    }

    /**
     * Deserialize an ItemStack from a CommentedConfigurationSection
     *
     * @param section The section to deserialize from
     * @param key     The key to deserialize from
     * @return The deserialized ItemStack
     */
    @Nullable
    public static ItemStack deserialize(@NotNull CommentedConfigurationSection section, @NotNull String key) {
        return deserialize(section, null, key, StringPlaceholders.empty());
    }

    /**
     * Deserialize an ItemStack from a CommentedConfigurationSection with placeholders
     *
     * @param section The section to deserialize from
     * @param sender  The CommandSender to apply placeholders from
     * @param key     The key to deserialize from
     * @return The deserialized ItemStack
     */
    @Nullable
    public static ItemStack deserialize(@NotNull CommentedConfigurationSection section, @Nullable CommandSender sender, @NotNull String key) {
        return deserialize(section, sender, key, StringPlaceholders.empty());
    }

    /**
     * Parse an integer from an object safely
     *
     * @param object The object
     * @return The integer
     */
    private static int toInt(String object) {
        try {
            return Integer.parseInt(object);
        } catch (NumberFormatException e) {
            return 0;
        }
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

        return HexUtils.colorify(PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(text)));
    }

    /**
     * Create an outline on a block using particles
     *
     * @param data   The particle to spawn
     * @param block  The block to outline
     * @param player The player to spawn the particles for
     */
    public static void outline(ParticleData data, Block block, Player player) {
        // Get the outline of the cube
        List<Location> cube = getCube(
                block.getLocation(),
                block.getLocation().clone().add(1, 1, 1),
                0.5
        );

        // Spawn all the particles within the cube
        BukkitTask particleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.get(), () -> {
            for (Location loc : cube) {
                data.spawn(player, loc, 1);
            }
        }, 0, 2);

        // Cancel the outline 1.5s later
        Bukkit.getScheduler().runTaskLater(EternalCrates.get(), particleTask::cancel, 35);
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

    /**
     * Get an enum from a string value
     *
     * @param enumClass The enum class
     * @param name      The name of the enum
     * @param <T>       The enum type
     * @return The enum
     */
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name) {
        return getEnum(enumClass, name, null);
    }

    /**
     * Get an enum from a string value
     *
     * @param enumClass The enum class
     * @param name      The name of the enum
     * @param def       The default value
     * @param <T>       The enum type
     * @return The enum
     */
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name, T def) {
        if (name == null) return def;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return def;
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
