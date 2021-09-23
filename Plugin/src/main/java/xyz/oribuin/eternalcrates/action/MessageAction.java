package xyz.oribuin.eternalcrates.action;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates; 
import xyz.oribuin.orilibrary.util.HexUtils;

public class MessageAction extends Action {

    @Override
    public String actionType() {
        return "MESSAGE";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player) {
        if (this.getMessage().length() == 0)
            return;

        player.sendMessage(HexUtils.colorify(this.getMessage()));
    }
}
