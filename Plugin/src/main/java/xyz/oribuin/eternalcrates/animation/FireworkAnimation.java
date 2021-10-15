package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.oribuin.eternalcrates.EternalCrates;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FireworkAnimation extends Animation {

    private final Map<Integer, CustomFirework> fireworkMap;
    private final long delay; // The delay between each firework spawn.

    public FireworkAnimation(String name, long delay) {
        super(name, AnimationType.FIREWORKS);
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
     */
    public void play(Location loc) {

        // Remove all the fireworks and register them again when the animation is played.
        this.fireworkMap.clear();
        this.registerFireworks(loc.clone());

        final AtomicInteger startNumber = new AtomicInteger();

        final TreeSet<Integer> treeSet = new TreeSet<>(this.fireworkMap.keySet());
        treeSet.forEach(integer -> {

            final CustomFirework customFirework = this.fireworkMap.get(integer);
            // Just because intellij is annoying.
            final World world = customFirework.location.getWorld();
            if (world == null)
                return;

            Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {

                // Spawn the firework
                Firework firework = world.spawn(customFirework.location, Firework.class, fireWork -> {
                    final FireworkMeta meta = fireWork.getFireworkMeta();

                    // Set meta because we're not trying to kill anyone here.
                    fireWork.setMetadata("eternalcrates:firework", new FixedMetadataValue(EternalCrates.getInstance(), true));
                    meta.addEffect(customFirework.effect);
                    fireWork.setFireworkMeta(meta);
                });

                firework.detonate();
                // Delay each effect by each firework that has been set off.
            }, startNumber.incrementAndGet() * delay);
        });
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
