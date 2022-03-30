package xyz.oribuin.eternalcrates.animation.defaults;

import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.AnimationType;

import java.util.HashMap;
import java.util.Map;

public class EmptyAnimation extends Animation {

    public EmptyAnimation() {
        super("none", AnimationType.NONE, "Oribuin", true);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>();
    }

    @Override
    public void load() {
        // do nothing
    }
}
