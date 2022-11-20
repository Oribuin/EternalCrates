package xyz.oribuin.eternalcrates.hook.item;

import com.jojodmo.itembridge.ItemBridge;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemBridgeItemProvider implements ItemProvider {

    private final boolean enabled;

    public ItemBridgeItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("ItemBridge");
    }

    @Override
    public ItemStack getItem(String key) {
        if (!this.enabled)
            return null;

        return ItemBridge.getItemStack(key);
    }

}
