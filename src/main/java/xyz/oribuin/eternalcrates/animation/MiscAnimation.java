package xyz.oribuin.eternalcrates.animation;

/**
 * Here you can create your own animations with no restrictions.
 * All you need to do is extend the MiscAnimation class and override the methods.
 * <p>
 * These animations are difficult to define in a single Category, so they are placed in their own category.
 *
 * @author Oribuin
 */
public abstract class MiscAnimation extends Animation {

    public MiscAnimation(String name, String author) {
        super(name, author, AnimationType.MISC, true);
    }

    public MiscAnimation(String name, String author, AnimationType type) {
        super(name, author, type, true);
    }

    public MiscAnimation(String name, String author, AnimationType type, boolean canBeVirtual) {
        super(name, author, type, canBeVirtual);
    }

}
