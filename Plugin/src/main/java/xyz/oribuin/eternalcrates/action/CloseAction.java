package xyz.oribuin.eternalcrates.action;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

public class CloseAction extends Action {

    @Override
    public String actionType() {
        return "CLOSE";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player, StringPlaceholders plc) {
        player.closeInventory();
    }

}
