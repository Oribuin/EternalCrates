package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class CelebrationAnimation extends FireworkAnimation {

    private Color primaryColor;
    private Color secondaryColor;

    public CelebrationAnimation() {
        super("celebration", "Oribuin", 10);
    }

    @Override
    public void registerFireworks(Location location) {

        final Location loc = location.clone().add(0, 2, 0);
        this.addFirework(loc.clone().subtract(1, 0, 1), this.ballFirework(this.primaryColor));
        this.addFirework(loc.clone().add(1, 0, -1), this.ballFirework(this.secondaryColor));

        this.addFirework(loc.clone().add(1, 0, 1), this.ballFirework(this.primaryColor));
        this.addFirework(loc.clone().subtract(1, 0, -1), this.ballFirework(this.secondaryColor));
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new LinkedHashMap<>() {{
            this.put("animation.primary-color", "#FFFFFF");
            this.put("animation-secondary-color", "#a9c0fe");
        }};
    }

    @Override
    public void load() {
        this.primaryColor = PluginUtils.fromHex(this.get("animation.primary-color", "#FF0000"));
        this.secondaryColor = PluginUtils.fromHex(this.get("animation.secondary-color", "#a9c0fe"));
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
