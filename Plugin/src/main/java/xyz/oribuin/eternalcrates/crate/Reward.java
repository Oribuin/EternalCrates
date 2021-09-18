package xyz.oribuin.eternalcrates.crate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import xyz.oribuin.gui.Item;

/**
 * @author Oribuin
 */
public class Reward {
    
    private final int id;
    private ItemStack displayItem;
    private Integer chance;
    private List<String> commands;

    public Reward(int id) {
        this.id = id;
        this.setDisplayItem(new Item.Builder(Material.BARRIER).setName(ChatColor.RED + "Unknown Item").create());
        this.chance = 10;
        this.commands = new ArrayList<>();
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

    public Integer getChance() {
        return chance;
    }

    public void setChance(Integer chance) {
        this.chance = chance;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

}
