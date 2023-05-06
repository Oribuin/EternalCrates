package xyz.oribuin.eternalcrates.hook.provider;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

public class OraxenItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "Oraxen";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {

        ItemBuilder itemBuilder = OraxenItems.getItemById(key);
        if (itemBuilder != null)
            return itemBuilder.build();

        return null;
    }

}
