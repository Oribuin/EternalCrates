package xyz.oribuin.eternalcrates.hook.items;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class MMOItemProvider implements ItemProvider {

    private final boolean enabled;

    public MMOItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("MMOItems");
    }

    @Override
    public ItemStack getItem(String key) {
        if (!this.enabled) {
            return null;
        }

        String[] parts = key.split(":", 2);
        if (parts.length != 2) {
            return null;
        }

        return MMOItems.plugin.getItem(parts[0], parts[1]);
    }

}
