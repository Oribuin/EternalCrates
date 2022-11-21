package xyz.oribuin.eternalcrates.animation;

public abstract class GuiAnimation extends Animation {

    public GuiAnimation(String name, String author) {
        super(name, author, AnimationType.GUI);
    }

    // TODO Add general gui animations

    /**
     * Different types of gui animations
     */
    public enum Type {
        SPINNING, COSMIC
    }

}
