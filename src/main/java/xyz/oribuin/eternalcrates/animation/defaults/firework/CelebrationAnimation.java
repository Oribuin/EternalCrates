package xyz.oribuin.eternalcrates.animation.defaults.firework;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.HashMap;
import java.util.Map;

public class CelebrationAnimation extends FireworkAnimation {

    private Color primaryColor = PluginUtils.fromHex("#FF0000");
    private Color secondaryColor = PluginUtils.fromHex("#a9c0fe");

    public CelebrationAnimation() {
        super("Celebration", "Oribuin", 10);
    }

    @Override
    public void registerFireworks(Location location) {

        final var loc = location.clone().add(0, 2, 0);
        this.addFirework(loc.clone().subtract(1, 0, 1), this.ballFirework(this.primaryColor));
        this.addFirework(loc.clone().add(1, 0, -1), this.ballFirework(this.secondaryColor));

        this.addFirework(loc.clone().add(1, 0, 1), this.ballFirework(this.primaryColor));
        this.addFirework(loc.clone().subtract(1, 0, -1), this.ballFirework(this.secondaryColor));
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("primary-color", "#FFFFFF");
            this.put("animation-secondary-color", "#a9c0fe");
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.primaryColor = PluginUtils.fromHex(config.getString("crate-settings.animation.primary-color"));
        this.secondaryColor = PluginUtils.fromHex(config.getString("crate-settings.animation.secondary-color"));
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
