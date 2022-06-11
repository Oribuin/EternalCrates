package xyz.oribuin.eternalcrates.hook.items;

import org.bukkit.inventory.ItemStack;

public interface ItemProvider {
    /**
     * Get the item stack for the given key
     *
     * @param key The key
     * @return The item stack
     */
    ItemStack getItem(String key);

}
