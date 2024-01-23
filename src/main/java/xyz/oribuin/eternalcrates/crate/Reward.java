package xyz.oribuin.eternalcrates.crate;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.util.CrateUtils;

import java.util.ArrayList;
import java.util.List;

public class Reward {

    private final String id;
    private ItemStack itemStack;
    private ItemStack previewItem;
    private double chance;
    private List<String> actions;

    public Reward(@NotNull String id, @NotNull ItemStack itemStack, double chance) {
        this.id = id;
        this.itemStack = itemStack;
        this.previewItem = itemStack;
        this.chance = chance;
        this.actions = new ArrayList<>();
    }

    /**
     * Give the physical reward to the player
     *
     * @param player The player who gets the reward
     */
    public void give(@NotNull Player player) {
        // TODO:  Check if the player has enough space in their inventory
        player.getInventory().addItem(this.itemStack.clone());
    }

    /**
     * Get the reward's display name.
     *
     * @return The reward's display name.
     */
    @SuppressWarnings("deprecation")
    public String getRewardName() {
        ItemStack item = this.previewItem;
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }

        return CrateUtils.formatEnum(item.getType().name());
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getPreviewItem() {
        return previewItem;
    }

    public void setPreviewItem(ItemStack previewItem) {
        this.previewItem = previewItem;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

}
