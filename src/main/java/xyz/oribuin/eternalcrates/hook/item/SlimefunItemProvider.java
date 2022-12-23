package xyz.oribuin.eternalcrates.hook.item;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlimefunItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "Slimefun";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        // Matching slimefun items
        // SlimefunItem#getItemById(String) is not used because it couldn't find items?
        for (var item : Slimefun.getRegistry().getSlimefunItemIds().entrySet()) {
            if (item.getKey().equalsIgnoreCase(key)) {
                return item.getValue().getItem().clone();
            }
        }

        return null;

    }
}
