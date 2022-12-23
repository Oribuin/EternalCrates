package xyz.oribuin.eternalcrates.hook.item;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "MMOItems";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        var parts = key.split(":", 2);
        if (parts.length != 2) {
            return null;
        }

        return MMOItems.plugin.getItem(parts[0], parts[1]);
    }

}
