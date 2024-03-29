package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.defaults.EmptyAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.ChickenAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.FireworkAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.FountainAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.MiniMeAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.SwordsAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.QuadAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.RingsAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.RippleAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.BunnyAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.PumpkinAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.SnowmanAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnimationManager extends Manager {

    private final Map<String, Animation> cachedAnimations = new HashMap<>();

    public AnimationManager(RosePlugin plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        this.rosePlugin.getLogger().info("Loading all the animations for the plugin.");

        // Add the default animations
        // Particle Animations
        this.cachedAnimations.put("rings", new RingsAnimation());
        this.cachedAnimations.put("ripple", new RippleAnimation());
        this.cachedAnimations.put("quad", new QuadAnimation());

        // Misc Animations
        this.cachedAnimations.put("fireworks", new FireworkAnimation());
        this.cachedAnimations.put("chicken", new ChickenAnimation());
        this.cachedAnimations.put("fountain", new FountainAnimation());
        this.cachedAnimations.put("mini-me", new MiniMeAnimation());
        this.cachedAnimations.put("swords", new SwordsAnimation());

        // Seasonal
        this.cachedAnimations.put("snowman", new SnowmanAnimation());
        this.cachedAnimations.put("pumpkin", new PumpkinAnimation());
        this.cachedAnimations.put("bunny", new BunnyAnimation());

        // Other
        this.cachedAnimations.put("none", new EmptyAnimation());


        this.rosePlugin.getLogger().info("Loaded " + this.cachedAnimations.size() + " EternalCrates animations .");
    }

    /**
     * Get an animation from the name.
     *
     * @param name The name of the animation
     * @return An optional animation.
     */
    public @Nullable Animation getAnimation(String name) {
        for (Animation animation : this.cachedAnimations.values()) {
            if (animation.getName().equalsIgnoreCase(name))
                return animation;
        }

        return null;
    }

    /**
     * Get an animation from the config;
     *
     * @param config The configuration file
     * @return The Optional Animation Type.
     */
    public @Nullable Animation getAnimation(final CommentedFileConfiguration config) {

        // get the base animation.
        final String animationName = config.getString("crate-settings.animation.name");
        if (animationName == null)
            return null;

        Animation animation = this.getAnimation(animationName.toLowerCase());

        if (animation == null) {
            this.rosePlugin.getLogger().warning("The animation " + animationName + " does not exist. Please check the config.");
            return null;
        }

        // Load the animation's config values
        animation.load(config);
        return animation;
    }

    /**
     * Get all animations from their type.
     *
     * @param type The type of animation.
     * @return A list of animations.
     */
    public List<Animation> getAnimationsFromType(AnimationType type) {
        return this.cachedAnimations.values().stream().filter(x -> x.getType() == type).collect(Collectors.toList());
    }

    /**
     * Register an animation into the plugin.
     *
     * @param animation The animation being registered.
     */
    public static void register(Animation animation) {
        final EternalCrates plugin = EternalCrates.getInstance();
        plugin.getLogger().info("Registered Crate Animation: " + animation.getName());
        plugin.getManager(AnimationManager.class).cachedAnimations.put(animation.getName().toLowerCase(), animation);

        final CrateManager crateManager = plugin.getManager(CrateManager.class);

        // Recreate any crates that weren't registered as animations.
        new HashSet<>(crateManager.getUnregisteredCrates().entrySet()).forEach(x -> {
            crateManager.getUnregisteredCrates().remove(x.getKey());

            File file = new File(plugin.getDataFolder() + "/crates/" + x.getKey() + ".yml");
            Crate crate = crateManager.createCreate(file, x.getValue());
            if (crate != null) {
                crateManager.getCachedCrates().put(crate.getId(), crate);
            }
        });
    }

    public Map<String, Animation> getCachedAnimations() {
        return cachedAnimations;
    }

    @Override
    public void disable() {
        this.cachedAnimations.clear();
    }

}