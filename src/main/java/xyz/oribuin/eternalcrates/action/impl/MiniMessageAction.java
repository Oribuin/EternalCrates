package xyz.oribuin.eternalcrates.action.impl;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

public class MiniMessageAction implements Action {

    private MiniMessage miniMessage;

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
        if (!NMSUtil.isPaper()) {
            Bukkit.getLogger().warning("MiniMessageAction is only supported on Paper servers.");
            return;
        }

        if (this.miniMessage == null) {
            this.miniMessage = MiniMessage.miniMessage();
        }

        StringPlaceholders plc = StringPlaceholders.of(
                "crate", crate.getName(),
                "player", player.getName(),
                "reward", reward.getRewardName()
        );

        Component message = miniMessage.deserialize(PlaceholderAPIHook.applyPlaceholders(player, plc.apply(input)));
        player.sendMessage(message);
    }

}
