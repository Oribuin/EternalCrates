package xyz.oribuin.eternalcrates.animation;

public enum AnimationType {

    GUI,
    PARTICLES,
    FIREWORKS,
    CUSTOM,
    SEASONAL,
    NONE;

    public static AnimationType fromString(String string) {
        for (AnimationType type : AnimationType.values()) {
            if (type.name().equalsIgnoreCase(string))
                return type;
        }
        return null;
    }

}
