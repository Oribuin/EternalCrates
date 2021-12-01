package xyz.oribuin.eternalcrates.action;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;

public class CloseAction extends Action {

    @Override
    public String actionType() {
        return "CLOSE";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player) {
        player.closeInventory();
    }

}
