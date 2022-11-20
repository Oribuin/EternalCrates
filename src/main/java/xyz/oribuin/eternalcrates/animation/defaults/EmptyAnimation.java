package xyz.oribuin.eternalcrates.animation.defaults;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.AnimationType;

import java.util.HashMap;
import java.util.Map;

public class EmptyAnimation extends Animation {

    public EmptyAnimation() {
        super("None", AnimationType.NONE, "Oribuin", true);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>();
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        // Do Nothing
    }

}
