package xyz.oribuin.eternalcrates.hook.item;

import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class OraxenItemProvider implements ItemProvider {

    private final boolean enabled;

    public OraxenItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("Oraxen");
    }

    @Override
    public ItemStack getItem(String key) {
        if (!this.enabled) {
            return null;
        }

        var itemBuilder = OraxenItems.getItemById(key);
        if (itemBuilder == null) {
            return null;
        }

        return itemBuilder.build();
    }

}
