package xyz.oribuin.eternalcrates.animation;

import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.gui.BaseGui;

import java.util.List;

public abstract class GuiAnimation extends Animation {

    private final int[] rotateSlots;
    private final int winningSlot;
    private final List<Reward> rewards;

    private Reward reward;
    private int startIndex;

    public GuiAnimation(String name, int[] rotateSlots) {
        super(name, AnimationType.GUI);
        this.rotateSlots = rotateSlots;
        this.winningSlot = rotateSlots[0];
        this.startIndex = 0;

        this.rewards = this.getRewards();
        this.reward = null;
    }

    public abstract List<Reward> getRewards();

    /**
     * Rotate all the items in the gui.
     * @param gui
     */
    public void rotateItems(BaseGui gui) {
        for (int i = 0; i < rotateSlots.length; i++) {
            int index = (startIndex + i) % rotateSlots.length;
            gui.setItem(this.rotateSlots[i], this.rewards.get(index).getDisplayItem().clone(), e -> {});
            gui.update();

            if (rotateSlots[i] == winningSlot)
                this.reward = this.rewards.get(index);

            startIndex--;
            if (startIndex < 0)
                startIndex = rotateSlots.length - 1;
        }
    }

    public int[] getRotateSlots() {
        return rotateSlots;
    }

    public int getWinningSlot() {
        return winningSlot;
    }

}
