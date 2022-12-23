package xyz.oribuin.eternalcrates.hook.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemProvider {

    /**
     * Get the name of the item provider
     *
     * @return The name of the item provider
     */
    String getPluginName();

    /**
     * Get the item stack for the given key
     *
     * @param key The key
     * @param player The player
     * @return The item stack
     */
    ItemStack getItem(@NotNull String key, @Nullable Player player);

}
