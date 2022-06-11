package xyz.oribuin.eternalcrates.hook.items;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderItemProvider implements ItemProvider {

    private final boolean enabled;

    public ItemsAdderItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    @Override
    public ItemStack getItem(String key) {
        if (!this.enabled) {
            return null;
        }

        CustomStack customStack = CustomStack.getInstance(key);
        if (customStack == null) {
            return null;
        }

        return customStack.getItemStack();
    }

}
