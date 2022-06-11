package xyz.oribuin.eternalcrates.crate;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oribuin
 */
public class Reward {

    private final int id;
    private ItemStack itemStack;
    private ItemStack previewItem;
    private double chance;
    private final List<Action> actions;

    public Reward(int id, ItemStack itemStack, double chance) {
        this.id = id;
        this.itemStack = itemStack;
        this.previewItem = itemStack;
        this.chance = chance;
        this.actions = new ArrayList<>();

    }

    /**
     * Execute a reward's specific actions
     *
     * @param player The player who gets the reward
     * @param plc    The string placeholders.
     */
    public void execute(Player player, Crate crate, StringPlaceholders plc) {
        plc.addPlaceholder("name", crate.getName());
        plc.addPlaceholder("player", player.getName());

        ItemStack item = this.getItemStack();
        if (item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            plc.addPlaceholder("reward", meta.hasDisplayName() ? meta.getDisplayName() : PluginUtils.format(item.getType()));
        }

        this.getActions().forEach(action -> action.execute(this, player, plc));
    }

    public void execute(Player player, Crate crate) {
        this.execute(player, crate, StringPlaceholders.empty());
    }

    public int getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public List<Action> getActions() {
        return actions;
    }

    public ItemStack getPreviewItem() {
        return previewItem;
    }

    public void setPreviewItem(ItemStack previewItem) {
        this.previewItem = previewItem;
    }
}
