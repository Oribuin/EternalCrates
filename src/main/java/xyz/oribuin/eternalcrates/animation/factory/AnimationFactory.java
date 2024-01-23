package xyz.oribuin.eternalcrates.animation.factory;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.manager.CrateManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class AnimationFactory {

    private static final Logger LOGGER = Logger.getLogger("EternalCrates/AnimationFactory");
    private static final Map<String, Class<? extends Animation>> ANIMATION_TYPES = new HashMap<>();
    private static AnimationFactory instance;

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

            ANIMATION_TYPES.put(animation.getId(), animationClass);
            checkUnregisteredCrates(animation.getId());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.severe("Failed to register animation " + animationClass.getSimpleName() + ". Error: " + e.getMessage());
        }
    }

    /**
     * Register a whole package into the factory
     *
     * @param packageName The package to register
     */
    public static void register(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes);
        Set<Class<? extends Animation>> subTypes = new HashSet<>(reflections.getSubTypesOf(Animation.class));
        subTypes.forEach(AnimationFactory::register);
    }

    /**
     * Create a new instance of the animation from the id
     *
     * @param id The id of the animation
     * @return The animation instance
     */
    public Animation find(String id) {
        if (!ANIMATION_TYPES.containsKey(id)) {
            return null;
        }

        try {
            Constructor<? extends Animation> constructor = ANIMATION_TYPES.get(id).getConstructor();
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
