package xyz.oribuin.eternalcrates.hook.items;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
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

        SlimefunItem item = SlimefunItem.getById(key);
        if (item == null) {
            return null;
        }

        return item.getItem().clone();
    }
}
