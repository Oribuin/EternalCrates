package xyz.oribuin.eternalcrates.action;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates; 

public interface Action {

    String actionType();

    void executeAction(EternalCrates plugin, Player player, String msg);

}
