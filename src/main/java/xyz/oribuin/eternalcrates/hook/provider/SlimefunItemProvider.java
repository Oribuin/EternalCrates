package xyz.oribuin.eternalcrates.hook.provider;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

import java.util.Map;

public class SlimefunItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "Slimefun";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {

        SlimefunItem slimefunItem = SlimefunItem.getById(key);
        if (slimefunItem != null)
            return slimefunItem.getItem().clone();

        return null;
    }

}
