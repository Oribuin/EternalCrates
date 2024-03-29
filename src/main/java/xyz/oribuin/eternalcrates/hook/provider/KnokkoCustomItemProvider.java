package xyz.oribuin.eternalcrates.hook.provider;

import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

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
