package xyz.oribuin.eternalcrates.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
    }

    /**
     * Set the ItemStack's Display Name.
     *
     * @param text The text.
     * @return ItemBuilder.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setName(String text) {
        final var meta = this.item.getItemMeta();
        if (meta == null || text == null)
            return this;

        meta.setDisplayName(text);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Set the ItemStack's Lore
     *
     * @param lore The lore
     * @return ItemBuilder.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setLore(List<String> lore) {
        final var meta = this.item.getItemMeta();
        if (meta == null || lore == null)
            return this;

        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Set the ItemStack's Lore
     *
     * @param lore The lore
     * @return ItemBuilder.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setLore(String... lore) {
        final var meta = this.item.getItemMeta();
        if (meta == null || lore == null)
            return this;

        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Set the ItemStack amount.
     *
     * @param amount The amount of items.
     * @return ItemBuilder
     */
    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Add an enchantment to an item.
     *
     * @param ench  The enchantment.
     * @param level The level of the enchantment
     * @return ItemBuilder
     */
    public ItemBuilder addEnchant(Enchantment ench, int level) {
        final var meta = this.item.getItemMeta();
        if (meta == null)
            return this;

        meta.addEnchant(ench, level, true);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Remove an enchantment from an Item
     *
     * @param ench The enchantment.
     * @return ItemBuilder
     */
    public ItemBuilder removeEnchant(Enchantment ench) {
        item.removeEnchantment(ench);
        return this;
    }

    /**
     * Remove and reset the ItemStack's Flags
     *
     * @param flags The ItemFlags.
     * @return ItemBuilder
     */
    public ItemBuilder setFlags(ItemFlag[] flags) {
        final var meta = this.item.getItemMeta();
        if (meta == null)
            return this;

        meta.removeItemFlags(ItemFlag.values());
        meta.addItemFlags(flags);
        item.setItemMeta(meta);

        return this;
    }


    /**
     * Change the item's unbreakable status.
     *
     * @param unbreakable true if unbreakable
     * @return ItemBuilder
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        final var meta = this.item.getItemMeta();
        if (meta == null)
            return this;

        meta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Set an item to glow.
     *
     * @return ItemBuilder
     */
    public ItemBuilder glow(boolean b) {
        if (!b)
            return this;

        final var meta = this.item.getItemMeta();
        if (meta == null)
            return this;

        meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Set an item's NBT Values
     *
     * @param key   The key to the nbt
     * @param value The value of the nbt
     * @return ItemBuilder
     */
    public ItemBuilder setNBT(Plugin plugin, String key, String value) {
        final var meta = item.getItemMeta();
        if (meta == null)
            return this;

        final var container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Set an item's NBT Values
     *
     * @param key   The key to the nbt
     * @param value The value of the nbt
     * @return ItemBuilder
     */
    public ItemBuilder setNBT(Plugin plugin, String key, int value) {
        final var meta = item.getItemMeta();
        if (meta == null)
            return this;

        final var container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Set an item's NBT Values
     *
     * @param key   The key to the nbt
     * @param value The value of the nbt
     * @return ItemBuilder
     */
    public ItemBuilder setNBT(Plugin plugin, String key, double value) {
        final var meta = item.getItemMeta();
        if (meta == null)
            return this;

        final var container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, key), PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setTexture(String texture) {
        if (item.getType() != Material.PLAYER_HEAD)
            return this;

        if (texture == null)
            return this;

        final var skullMeta = (SkullMeta) item.getItemMeta();
        if (skullMeta == null)
            return this;

        final Field field;
        try {
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            final var profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));

            field.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        item.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder setOwner(OfflinePlayer owner) {
        if (item.getType() != Material.PLAYER_HEAD)
            return this;

        if (owner == null)
            return this;

        final var skullMeta = (SkullMeta) item.getItemMeta();
        if (skullMeta == null)
            return this;

        skullMeta.setOwningPlayer(owner);
        item.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder setModel(int model) {
        final var meta = this.item.getItemMeta();
        if (meta == null)
            return this;

        meta.setCustomModelData(model);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addPotionEffect(PotionEffectType effectType, int duration, int amp) {
        if (!(this.item.getItemMeta() instanceof PotionMeta meta))
            return this;

        meta.addCustomEffect(new PotionEffect(effectType, duration, amp), true);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setPotionColor(Color color) {
        if (!(this.item.getItemMeta() instanceof PotionMeta meta))
            return this;

        meta.setColor(color);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Finalize the Item Builder and create the stack.
     *
     * @return The ItemStack
     */
    public ItemStack create() {
        return item;
    }

}
