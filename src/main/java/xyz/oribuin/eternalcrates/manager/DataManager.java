package xyz.oribuin.eternalcrates.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.CrateKeys;
import xyz.oribuin.eternalcrates.database.migration._1_CreateInitialTables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataManager extends AbstractDataManager {

    private final Map<UUID, CrateKeys> userKeys = new HashMap<>();

    private final Gson gson = new Gson();

    public DataManager(RosePlugin plugin) {
        super(plugin);
    }

    /**
     * Save a player's keys to the database
     *
     * @param player The player to save
     * @param keys   The keys to save
     */
    public void saveUser(UUID player, CrateKeys keys) {
        this.userKeys.put(player, keys);

        this.async(() -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "keys SET `items` = ? WHERE `player` = ?")) {
                statement.setString(1, this.gson.toJson(keys));
                statement.setString(2, player.toString());
                statement.executeUpdate();
            }
        }));

    }

    /*
     * Save the user's keys to the database
     *
     * @param player The player to save the keys for
     */
    public void saveUser(UUID player) {
        var crateKeys = this.userKeys.get(player);
        if (crateKeys == null)
            return;

        this.saveUser(player, crateKeys);
    }

    /**
     * Get the user's keys from the database
     *
     * @param player The player to get the keys for
     */
    public CrateKeys getUser(UUID player) {
        if (this.userKeys.get(player) != null) {
            return this.userKeys.get(player);
        }

        this.async(() -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement("SELECT `items` FROM " + this.getTablePrefix() + "keys WHERE `player` = ?")) {
                statement.setString(1, player.toString());
                var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    CrateKeys keys = this.gson.fromJson(resultSet.getString("items"), CrateKeys.class);
                    this.userKeys.put(player, keys);
                }
            }
        }));

        return this.userKeys.getOrDefault(player, new CrateKeys());
    }

    /**
     * Load the crate locations from the database
     *
     * @param crate The crate to load the locations for
     */
    public void loadCrateLocation(Crate crate) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement("SELECT `x`, `y`, `z`, `world` FROM " + this.getTablePrefix() + "crates WHERE `crateName` = ?")) {
                statement.setString(1, crate.getId());
                var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    Location loc = new Location(
                            Bukkit.getWorld(resultSet.getString("world")),
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z")
                    );

                    crate.getLocations().add(loc);
                }
            }
        }));
    }

    /**
     * Save the crate locations to the database
     *
     * @param crate The crate to save the locations for
     */
    public void saveCrateLocations(Crate crate) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            try (var statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "crates WHERE `crateName` = ?")) {
                statement.setString(1, crate.getId());
                statement.executeUpdate();
            }

            for (var loc : crate.getLocations()) {
                try (var statement = connection.prepareStatement("REPLACE INTO " + this.getTablePrefix() + "crates (`crateName`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)")) {
                    statement.setString(1, crate.getId());
                    statement.setDouble(2, loc.getX());
                    statement.setDouble(3, loc.getY());
                    statement.setDouble(4, loc.getZ());
                    statement.setString(5, loc.getWorld().getName());
                    statement.executeUpdate();

                }
            }
        }));
    }


    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(_1_CreateInitialTables.class);
    }

    /**
     * Get the user's keys from the database
     *
     * @param runnable
     */
    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.rosePlugin, runnable);
    }

    /**
     * Get the user's keys from the database
     *
     * @return The user's keys
     */
    public Map<UUID, CrateKeys> getUserKeys() {
        return userKeys;
    }


}
