package xyz.oribuin.eternalcrates.animation;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.gui.Gui;

import java.util.List;
import java.util.function.BiConsumer;

public class GuiAnimation extends Animation {

    private final int[] rotateSlots;

    private BiConsumer<Player, Gui> spinConsumer;

    private final int rotationCount;
    private final int rotationSpeed;
    private int startIndex;
    private int winningSlot;
    private int guiSize;

    public GuiAnimation(String name, String author, int[] rotateSlots) {
        super(name, AnimationType.GUI, author);
        this.rotateSlots = rotateSlots;
        this.winningSlot = rotateSlots[0];
        this.startIndex = 0;
        this.rotationCount = 3;
        this.rotationSpeed = 3;
    }

    /**
     * Rotate all the items in the gui.
     *
     * @param gui     The gui the items are being rotated in.
     * @param rewards The rewards being rotated.
     * @return The current reward that the gui has chosen.
     */
    public Reward rotateItems(Gui gui, List<Reward> rewards) {
        Reward reward = null;

        for (int i = 0; i < rotateSlots.length; i++) {
            int index = (startIndex + i) % rotateSlots.length;
            gui.setItem(this.rotateSlots[i], rewards.get(index).getDisplayItem().clone(), e -> {});
            gui.update();

            if (rotateSlots[i] == winningSlot)
                reward = rewards.get(index);
        }

        startIndex--;
        if (startIndex < 0)
            startIndex = rotateSlots.length - 1;

        return reward;
    }

    public int[] getRotateSlots() {
        return rotateSlots;
    }

    public int getGuiSize() {
        return guiSize;
    }

    public BiConsumer<Player, Gui> getSpinConsumer() {
        return spinConsumer;
    }

    public int getRotationCount() {
        return rotationCount;
    }

    public int getRotationSpeed() {
        return rotationSpeed;
    }

}
