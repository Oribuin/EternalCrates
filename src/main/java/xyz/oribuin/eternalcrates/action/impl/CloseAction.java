package xyz.oribuin.eternalcrates.action.impl;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

public class CloseAction implements Action {

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
        player.closeInventory();
    }

}
