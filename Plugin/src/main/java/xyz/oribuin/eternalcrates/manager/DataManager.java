package xyz.oribuin.eternalcrates.manager;

import org.bukkit.Location;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.orilibrary.database.DatabaseConnector;
import xyz.oribuin.orilibrary.manager.Manager;

import java.util.HashMap;
import java.util.Map;

public class DataManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final Map<Location, Crate> cachedCrates = new HashMap<>();

    private DatabaseConnector connector = null;

    public DataManager(EternalCrates plugin) {
        super(plugin);
    }

}
