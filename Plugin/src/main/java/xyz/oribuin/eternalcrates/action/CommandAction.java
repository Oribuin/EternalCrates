package xyz.oribuin.eternalcrates.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;

public class CommandAction implements Action {

    @Override
    public String actionType() {
        return "PLAYER";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player, String msg) {
        Bukkit.dispatchCommand(player, msg);
    }

}
