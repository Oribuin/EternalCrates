package xyz.oribuin.eternalcrates.hook.item;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import thirtyvirus.uber.UberItems;

public class UberItemProvider implements ItemProvider {

    private final boolean enabled;

    public UberItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("UberItems");
    }

    @Override
    public ItemStack getItem(String key) {

        if (!this.enabled) {
            return null;
        }

        var item = UberItems.getItem(key);
        if (item != null) {
            return item.makeItem(1);
        }

        var material = UberItems.getMaterial(key);
        if (material != null) {
            return material.makeItem(1);
        }

        return null;
    }
}
