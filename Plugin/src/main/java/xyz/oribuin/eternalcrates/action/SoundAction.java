package xyz.oribuin.eternalcrates.action;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates; 

public class SoundAction implements Action {

    @Override
    public String actionType() {
        return "SOUND";
    }

    @Override
    public void executeAction(EternalCrates plugin, Player player, String msg) {
        player.playSound(player.getLocation(), Sound.valueOf(msg), 1f, 1f);
    }

}
