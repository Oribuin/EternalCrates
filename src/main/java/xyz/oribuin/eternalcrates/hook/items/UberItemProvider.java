package xyz.oribuin.eternalcrates.hook.items;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import thirtyvirus.uber.UberItem;
import thirtyvirus.uber.UberItems;
import thirtyvirus.uber.UberMaterial;

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

        UberItem item = UberItems.getItem(key);
        if (item != null) {
            return item.makeItem(1);
        }

        UberMaterial material = UberItems.getMaterial(key);
        if (material != null) {
            return material.makeItem(1);
        }

        return null;
    }
}
