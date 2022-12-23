package xyz.oribuin.eternalcrates.hook.item;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicMobsItemProvider implements ItemProvider{
    @Override
    public String getPluginName() {
        return "MythicMobs";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        return MythicBukkit.inst().getItemManager().getItemStack(key);
    }
}
