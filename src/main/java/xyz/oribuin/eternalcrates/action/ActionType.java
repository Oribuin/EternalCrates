package xyz.oribuin.eternalcrates.action;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.action.impl.BroadcastAction;
import xyz.oribuin.eternalcrates.action.impl.CloseAction;
import xyz.oribuin.eternalcrates.action.impl.ConsoleAction;
import xyz.oribuin.eternalcrates.action.impl.GiveAction;
import xyz.oribuin.eternalcrates.action.impl.MessageAction;
import xyz.oribuin.eternalcrates.action.impl.MiniMessageAction;
import xyz.oribuin.eternalcrates.action.impl.PlayerAction;
import xyz.oribuin.eternalcrates.action.impl.SoundAction;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

import java.util.List;

public enum ActionType {
    BROADCAST(new BroadcastAction()),
    CLOSE(new CloseAction()),
    CONSOLE(new ConsoleAction()),
    GIVE(new GiveAction()),
    MESSAGE(new MessageAction()),
    MINIMESSAGE(new MiniMessageAction()),
    PLAYER(new PlayerAction()),
    SOUND(new SoundAction()),
    ;

    private final Action action;

    ActionType(Action action) {
        this.action = action;
    }


    /**
     * Run all the plugin actions through a series of comomands
     *
     * @param crate    The crate that was opened or used
     * @param player   The player to run the actions for
     * @param reward   The reward that was given to the player
     * @param commands The commands to run
     */
    public static void run(Crate crate, Player player, List<String> commands, Reward reward) {
        for (String command : commands) {
            ActionType type = match(command);

            if (type == null) {
                throw new IllegalArgumentException("Invalid action type: " + command);
            }


            String content = PlaceholderAPIHook.applyPlaceholders(player, command.substring(command.indexOf("]") + 1)); // remove "[action] ", todo: make removing the whitespace optional
            type.get().run(crate, player, reward, content);
        }
    }

    /**
     * Run all the plugin actions through a series of comomands
     *
     * @param player   The player to run the actions for
     * @param commands The commands to run
     */
    public static void run(Crate crate, Player player, List<String> commands) {
        run(crate, player, commands, null);
    }

    /**
     * Find the action type from the content
     *
     * @param content The content to find the action type from
     * @return The action type
     */
    private static ActionType match(String content) {
        // Match the [action] in the content
        String action = content.substring(1, content.indexOf("]"));

        // Loop through all the actions
        for (ActionType type : values()) {
            if (type.name().equalsIgnoreCase(action))
                return type;
        }

        return null;
    }

    public Action get() {
        return this.action;
    }

}
