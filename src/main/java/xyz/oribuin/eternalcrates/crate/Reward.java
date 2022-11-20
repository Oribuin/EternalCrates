package xyz.oribuin.eternalcrates.crate;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oribuin
 */
public class Reward {

    private final String id;
    private ItemStack itemStack;
    private ItemStack previewItem;
    private double chance;
    private final List<Action> actions;

    public Reward(String id, ItemStack itemStack, double chance) {
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

        var item = this.getItemStack();
        if (item.getItemMeta() != null) {
            var meta = item.getItemMeta();
            plc.addPlaceholder("reward", meta.hasDisplayName() ? meta.getDisplayName() : PluginUtils.formatEnum(item.getType().name()));
        }

        this.getActions().forEach(action -> action.execute(this, player, plc));
    }

    /**
     * Execute a reward's specific actions without placeholders
     *
     * @param player The player who gets the reward
     * @param crate  The crate the reward is from
     */
    public void execute(Player player, Crate crate) {
        this.execute(player, crate, StringPlaceholders.empty());
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
