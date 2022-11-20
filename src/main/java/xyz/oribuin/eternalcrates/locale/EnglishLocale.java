package xyz.oribuin.eternalcrates.locale;

import dev.rosewood.rosegarden.locale.Locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Oribuin";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<>() {{
            this.put("#1", "General Messages");
            this.put("prefix", "<g:#00B4DB:#0083B0>&lEternalCrates &8| &f");

            // General Messages
            this.put("#2", "Generic Command Messages");
            this.put("no-permission", "You don't have permission to execute this.");
            this.put("only-player", "This command can only be executed by a player.");
            this.put("unknown-command", "Unknown command, use #00B4DB/%cmd%&f help for more info");

            // Base Command
            this.put("#3", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/%cmd% help &efor command information.");

            // Reload Command
            this.put("#4", "Reload Command");
            this.put("command-reload-description", "Reloads the plugin.");
            this.put("command-reload-reloaded", "Configuration and locale files were reloaded");

            // Help Command
            this.put("#5", "Help Command");
            this.put("command-help-title", "&fAvailable Commands:");
            this.put("command-help-description", "Displays the help menu.");
            this.put("command-help-list-description", "&8 - #00B4DB/%cmd% %subcmd% %args% &7- %desc%");
            this.put("command-help-list-description-no-args", "&8 - #00B4DB/%cmd% %subcmd% &7- %desc%");

            // Animation Command
            this.put("#6", "Animation Command");
            this.put("command-animation-description", "Shows all available animations.");
            this.put("command-animation-header", "All animations of type #00B4DB%type%&f:");
            this.put("command-animation-format", " &f| #00B4DB&l%name% &7by &f%author%");
            this.put("command-animation-empty", "There are no animations of this type.");

            // Claim Command
            this.put("#7", "Claim Command");
            this.put("command-claim-description", "Claim any unclaimed crate keys.");
            this.put("command-claim-no-keys", "You have no unclaimed crate keys.");
            this.put("command-claim-success", "You have claimed #00B4DBx%to0tal% &fkey(s).");

            // Give Command
            this.put("#8", "Give Command");
            this.put("command-give-description", "Give a crate key to a player.");
            this.put("command-give-success", "You gave #00B4DBx%amount% &l%crate% &fkey(s) &fto #00B4DB&l%player%&f.");
            this.put("command-give-success-other", "You have received #00B4DBx%amount% &l%crate% &fkey(s).");
            this.put("command-give-full-inventory", "Your inventory is full, your key(s) have been placed in /crate claim.");

            this.put("#9", "Giveall Command");
            this.put("command-giveall-description", "Give all players online crate keys.");
            this.put("command-giveall-success", "You gave #00B4DBx%amount% &l%crate% &fkey(s) &fto all online players.");

            // List Command
            this.put("#10", "List Command");
            this.put("command-list-description", "List all available crates.");
            this.put("command-list-header", "All available crates:");
            this.put("command-list-format", " &f| #00B4DB&l%id% &7- [&f%reward_count%&7] rewards &f| &7[&f%locations%&7]");

            // Preview Command
            this.put("#11", "Preview Command");
            this.put("command-preview-description", "Preview a crate.");

            // Set Command
            this.put("#12", "Set Command");
            this.put("command-set-description", "Set a crate location.");
            this.put("command-set-success", "You have set a crate location for #00B4DB&l%crate%&f.");
            this.put("command-set-no-target", "You must be looking at a chest to set a crate location.");

            // General Crate Messages
            this.put("#13", "General Crate Messages");
            this.put("crate-remove-success", "You have successfully removed the crate location.");
            this.put("crate-open-no-keys", "You don't have an available key for this crate.");
            this.put("crate-open-invalid-key", "You don't have a valid key for this crate.");
            this.put("crate-open-no-slots", "You don't have enough inventory space to open this crate.");
            this.put("crate-open-using-crate", "You are already using a crate.");
            this.put("crate-open-animation-active", "This crate is already being opened.");

            // Argument Handler Messages
            this.put("#14", "Argument Handler Messages");
            this.put("argument-handler-animation", "%animation% is not a valid animation.");
            this.put("argument-handler-animation-type", "%type% is not a valid animation type.");
            this.put("argument-handler-crate", "%crate% is not a valid crate.");
            this.put("argument-handler-integer", "Integer [%input%] must be a whole number between -2^31 and 2^31-1 inclusively");
            this.put("argument-handler-player", "No Player with the username [%input%] was found online");
        }};
    }

}
