package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.GuiAnimation;
import xyz.oribuin.gui.Gui;

import java.util.function.BiConsumer;

public class WheelAnimation extends GuiAnimation {

    public WheelAnimation() {
        // Don't need to set the winning slot because it will default to the first number in the array
        super("wheel", new int[]{4, 5, 15, 24, 33, 41, 40, 39, 29, 20, 11, 3});
    }

    @Override
    public int getGuiSize() {
        return 45;
    }

    @Override
    public int getRotationCount() {
        return 3;
    }

    @Override
    public BiConsumer<Player, Gui> getSpinConsumer() {
        return (player, gui) -> player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 10f, 1f);
    }
}
