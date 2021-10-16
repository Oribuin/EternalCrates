package xyz.oribuin.eternalcrates.animation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.gui.Gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class GuiAnimation extends Animation {

    private final int[] rotateSlots;
    private final BiConsumer<Player, Gui> spinConsumer;
    private final int rotationCount;
    private final int rotationSpeed;
    private final int winningSlot;
    private final int guiSize;
    private int startIndex;
    private ItemStack fillerItem;
    private final Map<Integer, ItemStack> extras;

    public GuiAnimation(String name, String author, int[] rotateSlots) {
        super(name, AnimationType.GUI, author);
        this.rotateSlots = rotateSlots;
        this.winningSlot = rotateSlots[0];
        this.guiSize = 54;
        this.startIndex = 0;
        this.rotationCount = 3;
        this.rotationSpeed = 3;
        this.fillerItem = null;
        this.extras = new HashMap<>();
        this.spinConsumer = (player, gui) -> {};
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

    public ItemStack getFillerItem() {
        return fillerItem;
    }

    public void setFillerItem(ItemStack fillerItem) {
        this.fillerItem = fillerItem;
    }

    public Map<Integer, ItemStack> getExtras() {
        return extras;
    }

}
