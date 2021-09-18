package xyz.oribuin.eternalcrates.manager;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.orilibrary.manager.Manager;

import java.util.HashMap;
import java.util.Map;

public class CrateManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final Map<String, Crate> cachedCrates = new HashMap<>();

    public CrateManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        this.plugin.getLogger().info("Loading all the crates from the plugin.");
    }

    public Crate createNewCrate(FileConfiguration config) {
        final String name = config.getString("name");
    }


    public Map<String, Crate> getCachedCrates() {
        return cachedCrates;
    }

}
