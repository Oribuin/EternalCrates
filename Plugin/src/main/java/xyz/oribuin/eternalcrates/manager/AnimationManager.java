package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Material;
import org.bukkit.Particle;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;
import xyz.oribuin.eternalcrates.animation.GuiAnimation;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.CelebrationAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.ChickenAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.EmptyAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.FountainAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.MiniMeAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.PumpkinAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.QuadAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.RingsAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.RippleAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.SnowmanAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.SparkleAnimation;
import xyz.oribuin.eternalcrates.animation.defaults.SwordAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public class AnimationManager extends Manager {

    private final Map<String, Class<? extends Animation>> animationClasses = new HashMap<>();

    public AnimationManager(RosePlugin plugin) {
        super(plugin);
    }

    /**
     * Register an animation into the plugin.
     *
     * @param animation The animation being registered.
     */
    public static void register(Class<? extends Animation> animation) {
        final EternalCrates plugin = EternalCrates.getInstance();
        plugin.getLogger().info("Registered Crate Animation: " + animation.getName());
        plugin.getManager(AnimationManager.class).animationClasses.put(animation.getName(), animation);

        final CrateManager crateManager = plugin.getManager(CrateManager.class);

        // Recreate any crates that weren't registered as animations.
        new HashSet<>(crateManager.getUnregisteredCrates().entrySet()).forEach(x -> {
            crateManager.getUnregisteredCrates().remove(x.getKey());

            Crate crate = crateManager.createCreate(x.getValue());
            if (crate != null) {
                crateManager.getCachedCrates().put(crate.getId(), crate);
            }
        });
    }

    @Override
    public void reload() {
        this.rosePlugin.getLogger().info("Loading all the animations for the plugin.");

        // Add the default animations
        // GUI Animations
//        this.animationClasses.put("csgo", CsgoAnimation.class);
//        this.animationClasses.put("wheel", WheelAnimation.class);
        // Particle Animations
        this.animationClasses.put("rings", RingsAnimation.class);
        this.animationClasses.put("ripple", RippleAnimation.class);
        this.animationClasses.put("quad", QuadAnimation.class);
        // Firework Particles
        this.animationClasses.put("sparkle", SparkleAnimation.class); // we may need a better name for this.
        this.animationClasses.put("celebration", CelebrationAnimation.class);
        // Custom
        this.animationClasses.put("chicken", ChickenAnimation.class);
        this.animationClasses.put("fountain", FountainAnimation.class);
        this.animationClasses.put("mini-me", MiniMeAnimation.class);
        this.animationClasses.put("swords", SwordAnimation.class);
        // Seasonal
        this.animationClasses.put("snowman", SnowmanAnimation.class);
        this.animationClasses.put("pumpkin", PumpkinAnimation.class);
        // Hologram
        // TODO
        // Other
        this.animationClasses.put("none", EmptyAnimation.class);
    }

    /**
     * Get an animation from the name.
     *
     * @param name The name of the animation
     * @return An optional animation.
     */
    public Optional<Class<? extends Animation>> getAnimation(String name) {
        return this.animationClasses.get(name) == null ? Optional.empty() : Optional.of(this.animationClasses.get(name));
    }

    /**
     * Get an animation from the config;
     *
     * @param config The configuration file
     * @return The Optional Animation Type.
     */
    public Optional<? extends Animation> getAnimationFromConfig(final CommentedFileConfiguration config) {

        // get the base animation.
        Optional<Class<? extends Animation>> animationClass = this.getAnimation(config.getString("animation.name"));
        if (animationClass.isEmpty())
            return Optional.empty();

        Optional<? extends Animation> animation;

        try {
            animation = Optional.of(animationClass.get().getDeclaredConstructor().newInstance());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            return Optional.empty();
        }

        // Load the animation's config values
        animation.get().load();

        switch (animation.get().getAnimationType()) {
            case PARTICLES -> animation = this.getParticleAni(config, animation.get());
            case GUI -> animation = this.getGuiAnimation(config, animation.get());
            case FIREWORKS -> animation = Optional.of((FireworkAnimation) animation.get());
            case CUSTOM -> animation = Optional.of((CustomAnimation) animation.get());
            case NONE -> animation = Optional.of((EmptyAnimation) animation.get());
        }

        if (animation.isEmpty()) {
            return Optional.empty();
        }

        return animation;
    }

    /**
     * Get a particle animation from
     *
     * @param config    The configuration file
     * @param animation The animation
     * @return The particle animation.
     */
    private Optional<ParticleAnimation> getParticleAni(final CommentedFileConfiguration config, final Animation animation) {
        if (!(animation instanceof ParticleAnimation particleAni))
            return Optional.empty();

        Particle particle = Arrays.stream(Particle.values()).filter(x -> x.name().equalsIgnoreCase(config.getString("animation.particle")))
                .findFirst()
                .orElse(Particle.FLAME);

        final ParticleData particleData = new ParticleData(particle);
        particleData.setDustColor(PluginUtils.fromHex(PluginUtils.get(config, "animation.color", "#FFFFFF")));
        particleData.setTransitionColor(PluginUtils.fromHex(PluginUtils.get(config, "animation.transition", "#ff0000")));
        particleData.setNote(PluginUtils.get(config, "animation.note", 1));
        particleData.setItemMaterial(Material.matchMaterial(PluginUtils.get(config, "animation.item", "STONE")));
        particleData.setBlockMaterial(Material.matchMaterial(PluginUtils.get(config, "animation.block", "STONE")));

        particleAni.setParticleData(particleData);
        return Optional.of(particleAni);
    }

    /**
     * Get a gui animation values from the config.
     *
     * @param config    The file configuration
     * @param animation The base animation
     * @return The GUI Animation.
     */
    public Optional<GuiAnimation> getGuiAnimation(CommentedConfigurationSection config, Animation animation) {
        if (!(animation instanceof GuiAnimation gui))
            return Optional.empty();

        // TODO load gui animation values

        return Optional.of(gui);
    }


    public Map<String, Class<? extends Animation>> getAnimationClasses() {
        return animationClasses;
    }

    @Override
    public void disable() {
        // Unused
    }
}