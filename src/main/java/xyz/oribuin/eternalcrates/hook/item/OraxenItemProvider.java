package xyz.oribuin.eternalcrates.hook.item;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OraxenItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "Oraxen";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {

        var itemBuilder = OraxenItems.getItemById(key);
        if (itemBuilder == null) {
            return null;
        }

        return itemBuilder.build();
    }

}
