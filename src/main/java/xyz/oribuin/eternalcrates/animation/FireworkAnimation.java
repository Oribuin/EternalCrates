package xyz.oribuin.eternalcrates.animation;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FireworkAnimation extends Animation {

    private final Map<Integer, CustomFirework> fireworkMap;
    private final long delay; // The delay between each firework spawn.

    public FireworkAnimation(String name, String author, long delay) {
        super(name, AnimationType.FIREWORKS, author, true);
        this.delay = delay;
        this.fireworkMap = new HashMap<>();
    }

    /**
     * Register all the fireworks into the map.
     */
    public abstract void registerFireworks(Location location);

    /**
     * Add a firework to the animation map
     *
     * @param location The location relative to the crate location
     * @param firework The firework to be spawned.
     */
    public void addFirework(Location location, FireworkEffect firework) {
        this.fireworkMap.put(new AtomicInteger(this.fireworkMap.size()).incrementAndGet(), new CustomFirework(location, firework));
    }

    /**
     * Play the animation at the crate location
     *
     * @param loc    The location of the fireworks.
     * @param player The player who is opening the crate.
     */
    public void play(@NotNull Location loc, @NotNull Player player, @NotNull Crate crate) {
        if (this.isActive())
            return;

        this.setActive(true);
        // Remove all the fireworks and register them again when the animation is played.
        this.fireworkMap.clear();
        this.registerFireworks(loc);

        final var startNumber = new AtomicInteger();

        this.fireworkMap.keySet().forEach(integer -> {
            final CustomFirework customFirework = this.fireworkMap.get(integer);

            // Just because intellij is annoying.
            final World world = customFirework.location.getWorld();
            if (world == null)
                return;

            // Spawn the firework
            Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
                var firework = world.spawn(customFirework.location, Firework.class, fireWork -> {
                    final var meta = fireWork.getFireworkMeta();

                    // Set meta because we're not trying to kill anyone here.
                    fireWork.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                    meta.addEffect(customFirework.effect);
                    fireWork.setFireworkMeta(meta);
                });

                if (integer == this.fireworkMap.size()) {
                    crate.finish(player, loc);
                }

                firework.detonate();

                // Delay each effect by each firework that has been set off.
            }, integer == 0 ? 1 : startNumber.incrementAndGet() * delay);
        });
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>();
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        // Nothing to load
    }

    /**
     * Create a record for the custom firework object.
     *
     * @param location The location of the firework.
     * @param effect   The firework effect.
     */
    public record CustomFirework(Location location, FireworkEffect effect) {
    }

}
