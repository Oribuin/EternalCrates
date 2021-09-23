package xyz.oribuin.eternalcrates.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates; 

public class ConsoleAction extends Action {

    @Override
    public String actionType() {
        return "CONSOLE";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player) {
        if (this.getMessage().length() == 0)
            return;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.getMessage());
    }

}
