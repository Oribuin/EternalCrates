package xyz.oribuin.eternalcrates.action;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HSGamer
 */
public final class PluginAction {
    private static final Pattern ACTION_PATTERN = Pattern.compile("\\[(\\w+)]\\W?(.*)");
    private static final Map<String, Function<String, Action>> ACTIONS = new HashMap<>();

    static {
        registerAction("broadcast", BroadcastAction::new);
        registerAction("close", CloseAction::new);
        registerAction("console", ConsoleAction::new);
        registerAction("give", GiveAction::new);
        registerAction("message", MessageAction::new);
        registerAction("player", PlayerAction::new);
        registerAction("sound", SoundAction::new);
//        registerAction("title", TitleAction::new);
    }

    /**
     * Register an action
     *
     * @param name           Name of the action
     * @param actionFunction Function to create the action, with the message as a parameter
     */
    public static void registerAction(String name, Function<String, Action> actionFunction) {
        // toLowerCase to avoid case-sensitive issues
        ACTIONS.put(name.toLowerCase(Locale.ROOT), actionFunction);
    }

    /**
     * Register an action
     *
     * @param name           Name of the action
     * @param actionSupplier Supplier to create the action
     */
    public static void registerAction(String name, Supplier<Action> actionSupplier) {
        registerAction(name, s -> {
            Action action = actionSupplier.get();
            action.setMessage(s);
            return action;
        });
    }

    /**
     * Parse the action from text
     *
     * @param text Text to parse
     * @return Action associated with the text
     */
    public static Optional<Action> parse(String text) {
        // Check if the text matches the pattern ("[<action>] <message>") and get the action and message
        final Matcher matcher = ACTION_PATTERN.matcher(text);
        if (!matcher.find()) {
            return Optional.empty();
        }
        final String actionName = matcher.group(1).toLowerCase(Locale.ROOT); // toLowerCase to avoid case-sensitive issues
        final String actionText = matcher.group(2);

        Optional<Function<String, Action>> optional = Optional.ofNullable(ACTIONS.get(actionName));

        if (optional.isEmpty())
            return Optional.empty();

        Action action = optional.get().apply(actionText);
        return Optional.of(action);
    }

}