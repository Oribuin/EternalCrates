package xyz.oribuin.eternalcrates.animation;

public abstract class GuiAnimation extends Animation {

    private final GuiAnimation.Type type;

    public GuiAnimation(String name, String author, GuiAnimation.Type type) {
        super(name, AnimationType.GUI, author, true);
        this.type = type;
    }

    // TODO Add general gui animations

    public GuiAnimation.Type getType() {
        return this.type;
    }

    /**
     * Different types of gui animations
     */
    public enum Type {
        SPINNING, COSMIC
    }

}
