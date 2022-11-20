package xyz.oribuin.eternalcrates.animation;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParticleAnimation extends Animation {

    private ParticleData particleData = new ParticleData(Particle.FLAME);
    private int speed;
    private int length;

    public ParticleAnimation(String name, String author, int speed) {
        super(name, AnimationType.PARTICLES, author, true);
        this.speed = speed;
        this.length = 60;
    }

    public abstract List<Location> particleLocations(Location crateLocation);

    public abstract void updateTimer();

    public ParticleData getParticleData() {
        return particleData;
    }

    public void setParticleData(ParticleData particleData) {
        this.particleData = particleData;
    }

    /**
     * Spawn a particle at a location.
     *
     * @param loc   The location of the particle
     * @param count the amount of particles being spawned
     */
    public void play(Location loc, int count, Player player, Crate crate) {
        final var task = Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.getInstance(), () -> {
            this.updateTimer();
            this.particleLocations(loc.clone()).forEach(location -> particleData.spawn(player, location, count));
        }, 0, this.speed);

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> {
            task.cancel();
            crate.finish(player, loc);
        }, this.getLength());
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("animation.particle", "REDSTONE");
            this.put("animation.color", "#FFFFFF");
            this.put("animation.transition", "#ff0000");
            this.put("animation.note", 1);
            this.put("animation.item", "STONE");
            this.put("animation.block", "STONE");
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        var particle = Arrays.stream(Particle.values()).filter(x -> x.name().equalsIgnoreCase(config.getString("crate-settings.animation.particle")))
                .findFirst()
                .orElse(Particle.FLAME);

        this.particleData = new ParticleData(particle)
                .setDustColor(PluginUtils.fromHex(PluginUtils.get(config, "crate-settings.animation.color", "#FFFFFF")))
                .setTransitionColor(PluginUtils.fromHex(PluginUtils.get(config, "crate-settings.animation.transition", "#ff0000")))
                .setNote(PluginUtils.get(config, "crate-settings.animation.note", 1))
                .setItemMaterial(Material.matchMaterial(PluginUtils.get(config, "crate-settings.animation.item", "STONE")))
                .setBlockMaterial(Material.matchMaterial(PluginUtils.get(config, "crate-settings.animation.block", "STONE")));
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
