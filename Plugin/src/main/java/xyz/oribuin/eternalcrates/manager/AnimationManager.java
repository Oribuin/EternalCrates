package xyz.oribuin.eternalcrates.manager;

import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.defaults.CsgoAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.SpiralAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.WheelAnimation;
import xyz.oribuin.orilibrary.manager.Manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnimationManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final Map<String, Animation> cachedAnimations = new HashMap<>();

    public AnimationManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        this.plugin.getLogger().info("Loading all the animations for the plugin.");

        // Add the default
        registerAnimation(new CsgoAnimation());
        registerAnimation(new SpiralAnimation());
        registerAnimation(new WheelAnimation());
    }

    /**
     * Get an animation from the name.
     *
     * @param name The name of the animation
     * @return An optional animation.
     */
    public Optional<Animation> getAnimation(String name) {
        return Optional.ofNullable(this.cachedAnimations.get(name));
    }

    /**
     * Get a list of animations from the animation type
     *
     * @param type The animation type.
     * @return The list of animations.
     */
    public List<Animation> getAnimationFromType(AnimationType type) {
        return this.cachedAnimations.values().stream()
                .filter(animation -> animation.getAnimationType() == type)
                .collect(Collectors.toList());
    }

    public Map<String, Animation> getCachedAnimations() {
        return cachedAnimations;
    }

    /**
     * Register an animation into the plugin.
     *
     * @param animation The animation being registered.
     */
    public static void registerAnimation(Animation animation) {
        EternalCrates.getInstance().getLogger().info("Registered Crate Animation: " + animation.getName());
        EternalCrates.getInstance().getManager(AnimationManager.class).getCachedAnimations().put(animation.getName(), animation);
    }

}
