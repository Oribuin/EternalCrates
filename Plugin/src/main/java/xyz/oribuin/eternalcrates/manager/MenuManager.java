package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.eternalcrates.gui.ClaimGUI;
import xyz.oribuin.eternalcrates.gui.OriGUI;
import xyz.oribuin.eternalcrates.gui.PreviewGUI;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MenuManager extends Manager {

    private final Map<String, OriGUI> registeredGUIs = new HashMap<>();

    public MenuManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.registeredGUIs.put("preview-gui", new PreviewGUI(this.rosePlugin));
        this.registeredGUIs.put("claim-gui", new ClaimGUI(this.rosePlugin));

        this.registeredGUIs.forEach((name, gui) -> gui.load());
    }

    /**
     * Get a gui by name
     *
     * @param name The name of the gui
     * @return The gui
     */
    public Optional<OriGUI> getGUI(String name) {
        return Optional.ofNullable(this.registeredGUIs.get(name));
    }

    @Override
    public void disable() {
        this.registeredGUIs.clear();
    }

}
