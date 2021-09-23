package xyz.oribuin.eternalcrates.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates; 
import xyz.oribuin.orilibrary.util.HexUtils;

public class BroadcastAction implements Action {

    @Override
    public String actionType() {
        return "BROADCAST";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player, String msg) {
        Bukkit.broadcast(HexUtils.colorify(msg), "");
    }

}
