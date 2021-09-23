package xyz.oribuin.eternalcrates.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates; 

public class ConsoleAction implements Action {
    @Override
    public String actionType() {
        return "CONSOLE";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player, String msg) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg);
    }

}
