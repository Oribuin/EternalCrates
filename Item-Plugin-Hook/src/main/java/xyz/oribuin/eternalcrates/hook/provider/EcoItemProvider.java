package xyz.oribuin.eternalcrates.hook.provider;

import com.willfp.eco.core.items.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

/**
 * @author Esophose via RoseLoot
 */
public class EcoItemProvider implements ItemProvider {
    @Override
    public String getPluginName() {
        return "eco";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {

        ItemStack item = Items.lookup(key).getItem();

        return item.getType() != Material.AIR ? item : null;
    }

}
