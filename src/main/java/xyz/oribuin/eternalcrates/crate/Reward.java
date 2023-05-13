package xyz.oribuin.eternalcrates.crate;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.util.CrateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oribuin
 */
public class Reward {

    private final @NotNull String id; // The ID of the reward
    private @NotNull ItemStack itemStack; // The reward itemstack
    private @NotNull ItemStack previewItem; // The preview itemstack
    private double chance; // The chance of the reward
    private final @NotNull List<Action> actions; // The actions to perform when the reward is given

    public Reward(@NotNull String id, @NotNull ItemStack itemStack, double chance) {
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
     * @param crate  The crate the reward is from
     * @param plc    The string placeholders.
     */
    @SuppressWarnings("deprecation")
    public void execute(@NotNull Player player, @NotNull Crate crate, @NotNull StringPlaceholders.Builder plc) {
        plc.add("name", crate.getName());
        plc.add("player", player.getName());

        ItemStack item = this.getItemStack();
        if (item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            plc.add("reward", meta.hasDisplayName() ? meta.getDisplayName() : CrateUtils.formatEnum(item.getType().name()));
        }

        this.getActions().forEach(action -> action.execute(this, player, plc.build()));
    }

    /**
     * Execute a reward's specific actions without placeholders
     *
     * @param player The player who gets the reward
     * @param crate  The crate the reward is from
     */
    public void execute(@NotNull Player player, @NotNull Crate crate) {
        this.execute(player, crate, StringPlaceholders.builder());
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public @NotNull List<Action> getActions() {
        return actions;
    }

    public @NotNull ItemStack getPreviewItem() {
        return previewItem;
    }

    public void setPreviewItem(@NotNull ItemStack previewItem) {
        this.previewItem = previewItem;
    }
}
