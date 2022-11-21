package xyz.oribuin.eternalcrates.animation;

public enum AnimationType {

    GUI, // GUI Animations
    PARTICLES, // Animations that show particles spawn in specific shapes
    MISC, // Misc animations are animations that don't fit into the other categories.
    SEASONAL; // Animations that are centered around a holiday or season.

    public static AnimationType fromString(String string) {
        for (AnimationType type : AnimationType.values()) {
            if (type.name().equalsIgnoreCase(string))
                return type;
        }
        return null;
    }

}
