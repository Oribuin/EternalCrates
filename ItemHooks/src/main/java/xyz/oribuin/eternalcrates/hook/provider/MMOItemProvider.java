package xyz.oribuin.eternalcrates.hook.provider;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

public class MMOItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "MMOItems";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        String[] parts = key.split(":", 2);
        if (parts.length == 2) {
            MMOItems.plugin.getItem(parts[0], parts[1]);
        }

        return null;
    }

}
