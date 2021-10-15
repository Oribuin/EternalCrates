package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;

public class CelebrationAnimation extends FireworkAnimation {

    public CelebrationAnimation() {
        super("celebration", "Oribuin", 10);
    }

    @Override
    public void registerFireworks(Location location) {
        final Location loc = location.clone().add(0, 2, 0);
        addFirework(loc.clone().subtract(1, 0, 1), ballFirework(Color.WHITE));
        addFirework(loc.clone().add(1, 0, -1), ballFirework(Color.fromRGB(169, 191, 254)));

        addFirework(loc.clone().add(1, 0, 1), ballFirework(Color.WHITE));
        addFirework(loc.clone().subtract(1, 0, -1), ballFirework(Color.fromRGB(169, 191, 254)));
    }

    /**
     * Create the same firework effect but with a specified color
     *
     * @param color The color of the firework
     * @return the new firework effect.
     */
    private FireworkEffect ballFirework(Color color) {
        return FireworkEffect.builder()
                .withColor(color)
                .with(FireworkEffect.Type.BALL)
                .build();
    }

}
