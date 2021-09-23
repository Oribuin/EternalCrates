package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.GuiAnimation;
import xyz.oribuin.gui.Gui;

import java.util.function.BiConsumer;

public class CsgoAnimation extends GuiAnimation {

    public CsgoAnimation() {
        // Don't need to set the winning slot because it will default to the first number in the array
        super("csgo", new int[]
                {0, 1, 2, 3, 4, 5, 6, 7, 8,

                18, 19, 20, 21, 22, 23, 24, 25, 26});
    }

    @Override
    public int getGuiSize() {
        return 27;
    }

    @Override
    public int getRotationCount() {
        return 3;
    }

    @Override
    public int getRotationSpeed() {
        return 3;
    }

    @Override
    public BiConsumer<Player, Gui> getSpinConsumer() {
        return (player, gui) -> player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 50f, 1f);
    }
}
