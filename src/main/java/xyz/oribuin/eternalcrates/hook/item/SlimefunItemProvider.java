package xyz.oribuin.eternalcrates.hook.item;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class SlimefunItemProvider implements ItemProvider {

    private final boolean enabled;

    public SlimefunItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("Slimefun");
    }

    @Override
    public ItemStack getItem(String key) {
        if (!this.enabled) {
            return null;
        }

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
