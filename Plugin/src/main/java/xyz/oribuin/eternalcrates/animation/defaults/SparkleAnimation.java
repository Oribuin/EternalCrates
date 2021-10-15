package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;

public class SparkleAnimation extends FireworkAnimation {

    public SparkleAnimation() {
        super("sparkle", 5);
    }

    @Override
    public void registerFireworks(Location location) {
        final Location loc = location.clone().add(0.0, 6.0, 0.0);
        this.addFirework(subtracted(loc), this.effect(Color.RED)); // 6
        this.addFirework(subtracted(loc), this.effect(Color.ORANGE)); // 5
        this.addFirework(subtracted(loc), this.effect(Color.YELLOW)); // 4
        this.addFirework(subtracted(loc), this.effect(Color.LIME)); // 3
        this.addFirework(subtracted(loc), this.effect(Color.AQUA)); // 2
        this.addFirework(subtracted(loc), this.effect(Color.FUCHSIA)); // 1
    }

    /**
     * Create the same firework effect but with a specified color
     *
     * @param color The color of the firework
     * @return the new firework effect.
     */
    private FireworkEffect effect(Color color) {
        return FireworkEffect.builder()
                .withColor(color)
                .flicker(true)
                .with(FireworkEffect.Type.BALL)
                .build();
    }

    /**
     * The location with 1 Y axis subtracted from it, these methods are made for pure laziness
     *
     * @param loc The base location
     * @return The new subtracted location
     */
    private Location subtracted(Location loc) {
        return loc.subtract(0.0, 1.0, 0.0);
    }
}
