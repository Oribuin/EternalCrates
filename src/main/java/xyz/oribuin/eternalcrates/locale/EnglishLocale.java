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

            this.put("#2", "Generic Command Messages");
            this.put("no-permission", "You don't have permission to execute this.");
            this.put("only-player", "This command can only be executed by a player.");
            this.put("unknown-command", "Unknown command, use #00B4DB/%cmd%&f help for more info");

            this.put("#3", "Base Command");
            this.put("base-command-color", "#00B4DB");
            this.put("base-command-help", "&fUse #00B4DB/crates help &ffor command information.");

            this.put("#4", "Reload Command");
            this.put("command-reload-description", "Reloads the plugin.");
            this.put("command-reload-reloaded", "Configuration and locale files were reloaded");

            this.put("#5", "Help Command");
            this.put("command-help-title", "&fAvailable Commands:");
            this.put("command-help-description", "Displays the help menu.");
            this.put("command-help-list-description", "&8 - #00B4DB/%cmd% %subcmd% %args% &7- %desc%");
            this.put("command-help-list-description-no-args", "&8 - #00B4DB/%cmd% %subcmd% &7- %desc%");

        }};
    }

}
