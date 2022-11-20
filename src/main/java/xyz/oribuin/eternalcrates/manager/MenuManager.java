package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.eternalcrates.gui.PluginMenu;
import xyz.oribuin.eternalcrates.gui.PreviewGUI;

import java.util.HashMap;
import java.util.Map;

public class MenuManager extends Manager {

    private final Map<Class<? extends PluginMenu>, PluginMenu> registeredGUIs = new HashMap<>();

    public MenuManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.registeredGUIs.put(PreviewGUI.class, new PreviewGUI(this.rosePlugin));

        this.registeredGUIs.forEach((name, gui) -> gui.load());
    }

    /**
     * Get a registered GUI
     *
     * @param clazz The class of the GUI
     * @param <T>   The type of the GUI
     * @return The GUI
     */
    @SuppressWarnings("unchecked")
    public <T extends PluginMenu> T getGUI(Class<T> clazz) {
        return (T) this.registeredGUIs.get(clazz);
    }

    @Override
    public void disable() {
        this.registeredGUIs.clear();
    }

}
