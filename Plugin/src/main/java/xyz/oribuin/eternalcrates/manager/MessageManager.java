package xyz.oribuin.eternalcrates.manager;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class MessageManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();

    private FileConfiguration config;

    public MessageManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        final File file = FileUtils.createFile(plugin, "messages.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        // Set any values that dont exist
        Arrays.stream(Messages.values()).forEach(msg -> {
            final String key = msg.name().toLowerCase().replace("_", "-");
            if (config.get(key) == null)
                config.set(key, msg.value);
        });

        try {
            this.config.save(file);
        } catch (IOException ignored) {
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Send a configuration message without any placeholders
     *
     * @param receiver  The CommandSender who receives the message.
     * @param messageId The message path
     */
    public void send(CommandSender receiver, String messageId) {
        this.send(receiver, messageId, StringPlaceholders.empty());
    }

    /**
     * Send a configuration messageId with placeholders.
     *
     * @param receiver     The CommandSender who receives the messageId.
     * @param messageId    The messageId path
     * @param placeholders The Placeholders
     */
    public void send(CommandSender receiver, String messageId, StringPlaceholders placeholders) {
        final String msg = this.getConfig().getString(messageId);

        if (msg == null) {
            receiver.sendMessage(colorify("&c&lError &7| &fThis is an invalid message in the messages file, Please contact the server owner about this issue. (Id: " + messageId + ")"));
            return;
        }

        final String prefix = this.getConfig().getString("prefix");
        receiver.sendMessage(colorify(prefix + apply(receiver instanceof Player ? receiver : null, placeholders.apply(msg))));
    }

    /**
     * Send a raw message to the receiver without any placeholders
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver The message receiver
     * @param message  The raw message
     */
    public void sendRaw(CommandSender receiver, String message) {
        this.sendRaw(receiver, message, StringPlaceholders.empty());
    }

    /**
     * Send a raw message to the receiver with placeholders.
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver     The message receiver
     * @param message      The message
     * @param placeholders Message Placeholders.
     */
    public void sendRaw(CommandSender receiver, String message, StringPlaceholders placeholders) {
        receiver.sendMessage(colorify(apply(receiver instanceof Player ? receiver : null, placeholders.apply(message))));
    }

    public String get(String message) {
        return colorify(this.config.getString(message) != null ? this.config.getString(message) : Messages.valueOf(message.replace("-", "_")).value);
    }

    @Override
    public void disable() {

    }

    public String apply(CommandSender sender, String text) {
        return applyPapi(sender, text);
    }

    public static String applyPapi(CommandSender sender, String text) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            return text;

        return PlaceholderAPI.setPlaceholders(sender instanceof Player ? (Player) sender : null, text);
    }

    public enum Messages {
        PREFIX("#99ff99&lEternalCrates &8| &f"),
        PREVIEWING_CRATE("You are previewing %crate%!"),
        SAVED_CRATE("You have set this block to %crate%!"),
        UNSET_CRATE("You have removed the %crate% block!"),
        GAVE_KEY("You have given #99ff99%player% &fx%amount% #99ff99%crate%&f keys!"),
        GAVEALL_KEY("You have given everyone &fx%amount% #99ff99%crate%&f keys!"),
        INVALID_KEY("You need a %crate% key for this!"),
        GIVEN_KEY("You were given a %crate% key!"),
        SAVED_KEY("Your crate key has been sent to your /crates claim"),
        IN_ANIMATION("You cannot do this because the crate is in animation."),
        NOT_ENOUGH_SLOTS("You do not have enough free inventory slots to open this crate."),
        USING_CRATE("You are already opening a crate."),
        HELP_FORMAT("%prefix% &f%usage%"),
        LIST_FORMAT("%prefix% &f%crate% &7- #99ff99%rewards% &7rewards #99ff99(%location%)"),

        RELOAD("You have reloaded EternalCrates!"),
        DISABLED_WORLD("You cannot do this in this world."),
        NO_PERM("You do not have permission to do this."),
        INVALID_PLAYER("Please provide a correct player name."),
        INVALID_ARGS("Please use the correct command usage, %usage%"),
        INVALID_AMOUNT("&fPlease provide a valid number."),
        UNKNOWN_CMD("&fPlease include a valid command."),
        INVALID_CRATE("Please provide a valid crate name."),
        INVALID_BLOCK("Please look at a valid block"),
        PLAYER_ONLY("&fOnly a player can execute this command."),
        CONSOLE_ONLY("&fOnly console can execute this command.");

        private final String value;

        Messages(final String value) {
            this.value = value;
        }

    }
}