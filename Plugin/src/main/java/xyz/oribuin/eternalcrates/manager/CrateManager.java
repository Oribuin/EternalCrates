package xyz.oribuin.eternalcrates.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.orilibrary.manager.Manager;

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

    public Map<String, Crate> getCachedCrates() {
        return cachedCrates;
    }

}
