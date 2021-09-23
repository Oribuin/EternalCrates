package xyz.oribuin.eternalcrates.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates; 
import xyz.oribuin.orilibrary.util.HexUtils;

public class BroadcastAction extends Action {
    @Override
    public String actionType() {
        return "BROADCAST";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player) {
        if (this.getMessage().length() == 0)
            return;

        Bukkit.broadcast(HexUtils.colorify(this.getMessage()), "");
    }

}
