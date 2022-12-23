package xyz.oribuin.eternalcrates.hook.item;

import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KnokkoCustomItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "CustomItems";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        return CustomItemsApi.createItemStack(key, 1);
    }

}
