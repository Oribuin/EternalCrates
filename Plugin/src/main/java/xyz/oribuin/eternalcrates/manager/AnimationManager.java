package xyz.oribuin.eternalcrates.manager;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.*;
import xyz.oribuin.eternalcrates.animation.defaults.*;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.orilibrary.manager.Manager;

import java.util.*;
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

        // GUI Animations
        this.cachedAnimations.put("csgo", new CsgoAnimation());
        this.cachedAnimations.put("wheel", new WheelAnimation());
        // Particle Animations
        this.cachedAnimations.put("rings", new RingsAnimation());
        // Firework Particles
        this.cachedAnimations.put("sparkle", new SparkleAnimation()); // we may need a better name for this.
        this.cachedAnimations.put("celebration", new CelebrationAnimation());
        // Custom
        this.cachedAnimations.put("chicken", new ChickenAnimation());
        this.cachedAnimations.put("fountain", new FountainAnimation());
        // Hologram

        // Other
        this.cachedAnimations.put("none", new EmptyAnimation());
    }

    /**
     * Get an animation from the name.
     *
     * @param name The name of the animation
     * @return An optional animation.
     */
    public Optional<Animation> getAnimation(String name) {
        return this.cachedAnimations.values().stream()
                .filter(animation -> animation.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Get an animation from the config;
     *
     * @param config The configuration file
     * @return The Optional Animation Type.
     */
    public Optional<? extends Animation> getAnimationFromConfig(final FileConfiguration config) {

        // get the base optional animation.
        final Optional<Animation> optional = this.getAnimation(config.getString("animation.name"));
        if (optional.isEmpty())
            return Optional.empty();

        switch (optional.get().getAnimationType()) {
            case PARTICLES -> {
                return getParticleAni(config, optional.get());
            }
            case GUI -> {
                return Optional.of((GuiAnimation) optional.get());
            }
            case FIREWORKS -> {
                return Optional.of((FireworkAnimation) optional.get());
            }
            case CUSTOM -> {
                return Optional.of((CustomAnimation) optional.get());
            }
            case NONE -> {
                return Optional.of((EmptyAnimation) optional.get());
            }
            case HOLOGRAM -> {
            }

        }

        return Optional.empty();
    }

    private Optional<ParticleAnimation> getParticleAni(final FileConfiguration config, final Animation animation) {
        if (!(animation instanceof ParticleAnimation particleAni))
            return Optional.empty();

        Particle particle = Arrays.stream(Particle.values()).filter(x -> x.name().equalsIgnoreCase(config.getString("animation.particle")))
                .findFirst()
                .orElse(Particle.FLAME);

        final ParticleData particleData = new ParticleData(particle);

        particleData.setNote(config.getInt("animation.note"));
        if (config.getString("animation.color") != null)
            particleData.setDustColor(PluginUtils.fromHex(config.getString("animation.color")));

        if (config.getString("animation.transition") != null)
            particleData.setDustColor(PluginUtils.fromHex(config.getString("animation.transition")));

        if (config.getString("animation.item") != null)
            particleData.setItemMaterial(Material.matchMaterial(config.getString("animation.item")));

        if (config.getString("animation.block") != null)
            particleData.setBlockMaterial(Material.matchMaterial(config.getString("animation.block")));

        particleAni.setParticleData(particleData);
        return Optional.of(particleAni);
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
