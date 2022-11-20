package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import xyz.oribuin.eternalcrates.EternalCrates;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        PICKUP_IN_ANIMATION("crate-settings.item-pickup-in-animation", true, "Should players be able to pick up items mid animation?"),
        NO_KEY_VELOCITY("crate-settings.no-key-velocity", true, "Should the player be sent flying when failing to open a crate?");

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return EternalCrates.getInstance().getManager(ConfigurationManager.class).getConfig();
        }
    }

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }


    @Override
    protected String[] getHeader() {
        return new String[]{
                "___________ __                             .__  _________                __                 ",
                "\\_   _____//  |_  ___________  ____ _____  |  | \\_   ___ \\____________ _/  |_  ____   ______",
                " |    __)_\\   __\\/ __ \\_  __ \\/    \\\\__  \\ |  | /    \\  \\/\\_  __ \\__  \\\\   __\\/ __ \\ /  ___/",
                " |        \\|  | \\  ___/|  | \\/   |  \\/ __ \\|  |_\\     \\____|  | \\// __ \\|  | \\  ___/ \\___ \\ ",
                "/_______  /|__|  \\___  >__|  |___|  (____  /____/\\______  /|__|  (____  /__|  \\___  >____  >",
                "        \\/           \\/           \\/     \\/             \\/            \\/          \\/     \\/ "
        };
    }
}
