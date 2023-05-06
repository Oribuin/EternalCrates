package xyz.oribuin.eternalcrates.animation;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.CrateUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParticleAnimation extends Animation {

    private ParticleData particleData = new ParticleData(Particle.FLAME);
    private int speed;
    private int length;

    public ParticleAnimation(String name, String author) {
        super(name, author, AnimationType.PARTICLES, true);
    }

    /**
     * The function to get all the particle spawn locations
     *
     * @param location The location of the crate
     * @return A list of locations to spawn particles at
     */
    public abstract List<Location> particleLocations(Location location);

    /**
     * Function called before the particles are spawned
     */
    public abstract void updateTimer();

    /**
     * Play the particle animation
     *
     * @param loc    The location of the particles
     * @param player The player who is opening the crate
     * @param crate  The crate being opened
     */
    public void play(@NotNull Location loc, @NotNull Player player, @NotNull Crate crate) {
        final BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.getInstance(), () -> {
            this.updateTimer();
            this.particleLocations(loc.clone()).forEach(location -> particleData.spawn(player, location, 1));
        }, 0, this.speed);

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> {
            task.cancel();
            crate.finish(player, loc);
        }, this.length);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("particle", "REDSTONE");
            this.put("transition", "#ff0000");
            this.put("color", "#FFFFFF");
            this.put("note", 1);
            this.put("item", "STONE");
            this.put("block", "STONE");
            this.put("speed", 1);
            this.put("length", 60);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {

        this.speed = config.getInt("speed");
        this.length = config.getInt("length");

        Particle particle = Arrays.stream(Particle.values())
                .filter(x -> x.name().equalsIgnoreCase(config.getString("crate-settings.animation.particle")))
                .findFirst()
                .orElse(Particle.FLAME);

        Color baseColor = CrateUtils.fromHex(config.getString("crate-settings.animation.color"));

        this.particleData = new ParticleData(particle)
                .setDustOptions(baseColor)
                .setDustTransition(baseColor, CrateUtils.fromHex(config.getString("crate-settings.animation.transition")))
                .setNote(config.getInt("crate-settings.animation.note"))
                .setItemStackData(Material.matchMaterial(config.getString("crate-settings.animation.item", "BLACK_WOOL")))
                .setMaterialData(Material.matchMaterial(config.getString("crate-settings.animation.block", "BLACK_WOOL")));
    }

    public ParticleData getParticleData() {
        return particleData;
    }

    public void setParticleData(ParticleData particleData) {
        this.particleData = particleData;
    }

}
