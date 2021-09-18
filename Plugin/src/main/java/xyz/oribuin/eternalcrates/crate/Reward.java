package xyz.oribuin.eternalcrates.crate;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import xyz.oribuin.gui.Item;

/**
 * @author Oribuin
 */
public class Reward {
    
    private final int id;
    private ItemStack displayItem;
    private final Double chance;
    private Map<CommandSender, String> commandMap;

    public Reward(int id) {
        this.id = id;
        this.setDisplayItem(new Item.Builder(Material.BARRIER).setName(ChatColor.RED + "Unknown Item").create());
        this.chance = 10.0;
        this.setCommandMap(new HashMap<>());
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

    public Double getChance() {
        return chance;
    }

    public Map<CommandSender, String> getCommandMap() {
        return commandMap;
    }

    public void setCommandMap(Map<CommandSender, String> commandMap) {
        this.commandMap = commandMap;
    }

}
