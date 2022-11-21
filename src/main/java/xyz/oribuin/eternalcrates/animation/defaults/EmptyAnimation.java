package xyz.oribuin.eternalcrates.animation.defaults;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.HashMap;
import java.util.Map;

public class EmptyAnimation extends Animation {

    public EmptyAnimation() {
        super("None", "Oribuin");
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>();
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        // Do Nothing
    }

    @Override
    public void play(@NotNull Location loc, @NotNull Player player, @NotNull Crate crate) {
        crate.finish(player, loc);
    }

}
