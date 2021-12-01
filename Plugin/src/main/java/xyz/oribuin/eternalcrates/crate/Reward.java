package xyz.oribuin.eternalcrates.crate;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.gui.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oribuin
 */
public class Reward {

    private final int id;
    private final List<Action> actions;
    private ItemStack displayItem;
    private double chance;

    public Reward(int id) {
        this.id = id;
        this.setDisplayItem(new Item.Builder(Material.BARRIER).setName(ChatColor.RED + "Unknown Item").create());
        this.chance = 10;
        this.actions = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
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

}
