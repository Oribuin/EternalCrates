package xyz.oribuin.eternalcrates.animation.factory;

import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.impl.BunnyAnimation;
import xyz.oribuin.eternalcrates.animation.impl.ChickenAnimation;
import xyz.oribuin.eternalcrates.animation.impl.DefaultAnimation;
import xyz.oribuin.eternalcrates.animation.impl.FountainAnimation;
import xyz.oribuin.eternalcrates.animation.impl.QuadAnimation;
import xyz.oribuin.eternalcrates.animation.impl.RippleAnimation;
import xyz.oribuin.eternalcrates.manager.CrateManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class AnimationFactory {

    private static final Logger LOGGER = Logger.getLogger("EternalCrates/AnimationFactory");
    private static final Map<String, Class<? extends Animation>> ANIMATION_TYPES = new HashMap<>();
    private static AnimationFactory instance;

    static {
        register(BunnyAnimation.class);
        register(ChickenAnimation.class);
        register(DefaultAnimation.class);
        register(FountainAnimation.class);
        register(QuadAnimation.class);
        register(RippleAnimation.class);
    }

    /**
     * Register an animation into the factory
     *
     * @param animationClass The animation class to register
     */
    public static void register(Class<? extends Animation> animationClass) {

        try {
            Constructor<? extends Animation> constructor = animationClass.getConstructor();
            Animation animation = constructor.newInstance();

            if (ANIMATION_TYPES.containsKey(animation.getId()) || ANIMATION_TYPES.containsValue(animationClass)) {
                LOGGER.warning("Animation " + animationClass.getSimpleName() + " is already registered.");
                return;
            }

            ANIMATION_TYPES.put(animation.getId().toLowerCase(), animationClass);
            checkUnregisteredCrates(animation.getId());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.severe("Failed to register animation " + animationClass.getSimpleName() + ". Error: " + e.getMessage());
        }
    }


    /**
     * Create a new instance of the animation from the id
     *
     * @param id The id of the animation
     * @return The animation instance
     */
    public Animation find(String id) {
        Class<? extends Animation> animationClass = ANIMATION_TYPES.get(id.toLowerCase());
        if (animationClass == null) return null;

        try {
            Constructor<? extends Animation> constructor = animationClass.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.severe("Failed to get animation " + id + ". Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if this animation is part of the crates that were not registered
     * due to having an invalid animation
     */
    private static void checkUnregisteredCrates(String animationId) {
        CrateManager manager = EternalCrates.get().getManager(CrateManager.class);
        manager.getUnregisteredCrates().forEach((file, string) -> {
            if (string.equalsIgnoreCase(animationId)) {
                manager.create(file);
            }
        });

    }

    /**
     * Get all the animation id keys
     *
     * @return The animation ids
     */
    public Set<String> keys() {
        return ANIMATION_TYPES.keySet();
    }

    /**
     * Get the animation factory instance or create a new one
     *
     * @return The animation factory instance
     */
    public static AnimationFactory get() {
        if (instance == null) {
            instance = new AnimationFactory();
        }

        return instance;
    }


}
