package xyz.oribuin.eternalcrates.action.impl;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.util.CrateUtils;

public class SoundAction implements Action {

    /**
     * The functionality this action provides when its ran
     *
     * @param crate  The crate that was opened or used
     * @param player The player that opened or used the crate
     * @param reward The reward that was given to the player
     * @param input  The content provided by the user
     */
    @Override
    public void run(Crate crate, Player player, Reward reward, String input) {
        String[] split = input.split(";");

        Sound sound = CrateUtils.getEnum(Sound.class, split[0], Sound.ENTITY_PLAYER_LEVELUP);
        float volume = 50.0f;
        if (split.length > 1) {
            volume = Float.parseFloat(split[1]);
        }

        player.playSound(player.getLocation(), sound, volume, 1.0f);
    }

}
