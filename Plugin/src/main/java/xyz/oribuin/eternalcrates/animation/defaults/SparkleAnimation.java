package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;

public class SparkleAnimation extends FireworkAnimation {

    public SparkleAnimation() {
        super("sparkle", "Oribuin", 3);
    }

    @Override
    public void registerFireworks(Location location) {
        final Location loc = location.clone().add(0.0, 7.0, 0.0);
        this.addFirework(loc.clone().subtract(0.0, 1.0, 0.0), this.effect(Color.RED)); // 6
        this.addFirework(loc.clone().subtract(0.0, 2.0, 0.0), this.effect(Color.ORANGE)); // 5
        this.addFirework(loc.clone().subtract(0.0, 3.0, 0.0), this.effect(Color.YELLOW)); // 4
        this.addFirework(loc.clone().subtract(0.0, 4.0, 0.0), this.effect(Color.LIME)); // 3
        this.addFirework(loc.clone().subtract(0.0, 5.0, 0.0), this.effect(Color.AQUA)); // 2
        this.addFirework(loc.clone().subtract(0.0, 6.0, 0.0), this.effect(Color.FUCHSIA)); // 1
        this.addFirework(loc.clone().subtract(0.0, 7.0, 0.0), FireworkEffect.builder()
                .withColor(Color.fromRGB(106, 13, 173))
                .with(FireworkEffect.Type.BURST)
                .build());
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
                .with(FireworkEffect.Type.BALL)
                .build();
    }

}
