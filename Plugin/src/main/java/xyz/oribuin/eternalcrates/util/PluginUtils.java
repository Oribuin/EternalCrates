package xyz.oribuin.eternalcrates.util;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang.WordUtils;
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
import xyz.oribuin.eternalcrates.nms.NMSAdapter;
import xyz.oribuin.eternalcrates.nms.NMSHandler;
import xyz.oribuin.gui.Item;

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
     * Format a material name through this long method
     *
     * @param material The material
     * @return The material name.
     */
    public static String format(Material material) {
        return WordUtils.capitalizeFully(material.name().toLowerCase().replace("_", " "));
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

        ItemStack baseItem = new ItemStack(Material.STONE);

        final String plugin = get(config, path + ".plugin", null);

        if (plugin != null) {
            baseItem = CustomItemPlugin.parse(plugin);
        } else {
            Material material = Material.getMaterial(get(config, path + ".material", "STONE"));
            if (material != null) {
                baseItem = new ItemStack(material);
            }
        }

        if (baseItem == null) {
            System.out.println("[CustomItems] Error: Could not get item from path " + path);
            return new ItemStack(Material.BARRIER);
        }

        // Format the item lore
        List<String> lore = get(config, path + ".lore", List.of());
        lore = lore.stream().map(s -> format(player, s, placeholders)).collect(Collectors.toList());

        // Get item flags
        ItemFlag[] flags = get(config, path + ".flags", new ArrayList<String>())
                .stream()
                .map(String::toUpperCase)
                .map(ItemFlag::valueOf)
                .toArray(ItemFlag[]::new);

        // Build the item stack
        Item.Builder builder = new Item.Builder(baseItem)
                .setName(get(config, path + ".name", null))
                .setLore(lore)
                .setAmount(Math.max(get(config, path + ".amount", 1), 1))
                .setFlags(flags)
                .glow(get(config, path + ".glow", false))
                .setTexture(get(config, path + ".texture", null))
                .setPotionColor(fromHex(get(config, path + ".potion-color", null)))
                .setModel(get(config, path + ".model-data", -1));

        // Get item owner
        String owner = get(config, path + ".owner", null);
        if (owner != null)
            builder.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(owner)));

        // Get item enchantments
        final CommentedConfigurationSection enchants = config.getConfigurationSection(path + "enchants");
        if (enchants != null) {
            enchants.getKeys(false).forEach(key -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
                if (enchantment == null)
                    return;

                builder.addEnchant(enchantment, enchants.getInt(key));
            });
        }

        ItemStack item = builder.create();

        // Get item nbt
        final CommentedConfigurationSection nbt = config.getConfigurationSection(path + "nbt");
        if (nbt != null) {
            NMSHandler handler = NMSAdapter.getHandler();

            for (String s : nbt.getKeys(false)) {
                Object obj = nbt.get(s);

                // this is a goddamn sin, I hate this
                if (obj instanceof String)
                    item = handler.setString(item, s, nbt.getString(s));

                // you've coded for 3 years and can't do it any better?
                if (obj instanceof Long)
                    item = handler.setLong(item, s, nbt.getLong(s));

                // lord no
                if (obj instanceof Integer)
                    item = handler.setInt(item, s, nbt.getInt(s));

                // please make it stop
                if (obj instanceof Boolean)
                    item = handler.setBoolean(item, s, nbt.getBoolean(s));

                // goddamn
                if (obj instanceof Double)
                    item = handler.setDouble(item, s, nbt.getDouble(s));

                // thank god its over
            }
        }

        return item;
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
        return HexUtils.colorify(PAPI.apply(player, placeholders.apply(text)));
    }

}
