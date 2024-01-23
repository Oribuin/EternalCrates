package xyz.oribuin.eternalcrates.action;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

public interface Action {

    /**
     * The functionality this action provides when its ran
     *
     * @param crate  The crate that was opened or used
     * @param player The player that opened or used the crate
     * @param reward The reward that was given to the player
     * @param input  The content provided by the user
     */
    void run(Crate crate, Player player, Reward reward, String input);

}