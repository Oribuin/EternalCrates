package xyz.oribuin.eternalcrates.manager;

import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.orilibrary.manager.Manager;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final Map<String, Animation> cachedAnimations = new HashMap<>();

    public AnimationManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        this.plugin.getLogger().info("Loading all the animations for the plugin.");
    }

    public Map<String, Animation> getCachedAnimations() {
        return cachedAnimations;
    }

}
