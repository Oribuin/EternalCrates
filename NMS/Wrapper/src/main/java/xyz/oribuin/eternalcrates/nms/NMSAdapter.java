package xyz.oribuin.eternalcrates.nms;

import org.bukkit.Bukkit;

public final class NMSAdapter {

    private static NMSHandler handler;

    static {
        try {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String version = name.substring(name.lastIndexOf('.') + 1);
            handler = (NMSHandler) Class.forName("xyz.oribuin.eternalcrates.nms." + version + ".NMSHandlerImpl").getConstructor().newInstance();
        } catch (Exception ignored) {
        }
    }

    /**
     * @return true if this server version is supported, false otherwise
     */
    public static boolean isValidVersion() {
        return handler != null;
    }

    /**
     * @return the instance of the NMSHandler, or null if this server version is not supported
     */
    public static NMSHandler getHandler() {
        return handler;
    }

}

