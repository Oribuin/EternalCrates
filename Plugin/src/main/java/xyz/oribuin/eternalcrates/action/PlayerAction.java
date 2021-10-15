package xyz.oribuin.eternalcrates.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.util.HexUtils;

public class PlayerAction extends Action {

    @Override
    public String actionType() {
        return "PLAYER";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player) {
        if (this.getMessage().length() == 0)
            return;

        Bukkit.dispatchCommand(player, HexUtils.colorify(MessageManager.applyPapi(player, this.getMessage())));
    }

}
