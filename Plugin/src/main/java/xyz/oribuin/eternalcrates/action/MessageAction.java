package xyz.oribuin.eternalcrates.action;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates; 
import xyz.oribuin.orilibrary.util.HexUtils;

public class MessageAction implements Action {
    @Override
    public String actionType() {
        return "MESSAGE";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player, String msg) {
        player.sendMessage(HexUtils.colorify(msg));
    }
}
