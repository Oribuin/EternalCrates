package xyz.oribuin.eternalcrates.action;

import java.util.Optional;

public enum PluginAction {
    BROADCAST(new BroadcastAction()),
    CLOSE(new CloseAction()),
    CONSOLE(new ConsoleAction()),
    GIVE(new GiveAction()),
    MESSAGE(new MessageAction()),
    PLAYER(new PlayerAction()),
    SOUND(new SoundAction());
//    TILE(new TitleAction());

    final Action action;

    PluginAction(Action action) {
        this.action = action;
    }

    /**
     * Get the action associated with this enum
     *
     * @param action Action to get
     * @return Action associated with this enum
     */
    public static PluginAction getAction(String action) {
        for (PluginAction a : PluginAction.values()) {
            if (a.name().equalsIgnoreCase(action)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Parse the action from text
     *
     * @param text Text to parse
     * @return Action associated with the text
     */
    public static Optional<Action> parse(String text) {
        // Find an action by "[action]" and get the text after it.
        final String[] message = text.split("\\[")[1].split("\\]");
        if (message.length < 2)
            return Optional.empty();

        final String actionName = message[0];
        final String actionText = message[1];

        Optional<PluginAction> optional = Optional.ofNullable(getAction(actionName));

        if (optional.isEmpty())
            return Optional.empty();

        Action action = optional.get().action.clone();
        action.setMessage(actionText);
        return Optional.of(action);
    }

}
