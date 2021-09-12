package xyz.oribuin.eternalcrates;

import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.orilibrary.OriPlugin;

import java.util.HashSet;
import java.util.Set;

public class EternalCrates extends OriPlugin {

    private static EternalCrates instance;
    private final Set<Animation> animationSet = new HashSet<>();

    @Override
    public void enablePlugin() {

        instance = this;

        // Load Plugin Managers Asynchronously.
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            // TODO
        });


    }

    @Override
    public void disablePlugin() {

    }

    public Set<Animation> getAnimationSet() {
        return animationSet;
    }

    /**
     * Register an animation into the plugin.
     *
     * @param animation The animation being registered.
     */
    public static void registerAnimation(Animation animation) {
        instance.getLogger().info("Registered Crate Animation: " + animation.getName());
        instance.animationSet.add(animation);
    }

    public static EternalCrates getInstance() {
        return instance;
    }

}
