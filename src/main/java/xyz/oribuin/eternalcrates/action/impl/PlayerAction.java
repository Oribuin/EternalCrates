package xyz.oribuin.eternalcrates.action.impl;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

public class PlayerAction implements Action {

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
        StringPlaceholders plc = StringPlaceholders.of(
                "crate", crate.getName(),
                "player", player.getName(),
                "reward", reward.getRewardName()
        );

        String command = PlaceholderAPIHook.applyPlaceholders(player, plc.apply(input));
        Bukkit.dispatchCommand(player, command);
    }

}