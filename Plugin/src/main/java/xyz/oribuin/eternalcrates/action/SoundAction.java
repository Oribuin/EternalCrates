package xyz.oribuin.eternalcrates.action;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

public class SoundAction extends Action {

    @Override
    public String actionType() {
        return "SOUND";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player, StringPlaceholders plc) {
        if (this.getMessage().length() == 0)
            return;

        player.playSound(player.getLocation(), Sound.valueOf(this.getMessage()), 100f, 1f);
    }

}
