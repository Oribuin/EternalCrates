package xyz.oribuin.eternalcrates.hook.item;

import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class KnokkoCustomItemProvider implements ItemProvider {

    private final boolean enabled;

    public KnokkoCustomItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("CustomItems");
    }

    @Override
    public ItemStack getItem(String key) {
        if (!this.enabled) {
            return null;
        }

        return CustomItemsApi.createItemStack(key, 1);
    }

}
