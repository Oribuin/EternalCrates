package xyz.oribuin.eternalcrates.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.CrateKeys;
import xyz.oribuin.eternalcrates.database.migration._1_CreateInitialTables;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<Void> save(UUID player, CrateKeys keys) {
        return CompletableFuture.runAsync(() -> {
            this.userKeys.put(player, keys);

            this.databaseConnector.connect(connection -> {
                try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO " + this.getTablePrefix() + "keys (`player`, `items`) VALUES (?, ?)")) {
                    statement.setString(1, player.toString());
                    statement.setString(2, this.gson.toJson(keys));
                    statement.executeUpdate();
                }
            });
        });
    }


    /**
     * Get the user's keys from the database
     *
     * @param player The player to get the keys for
     */
    public CompletableFuture<CrateKeys> user(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            // Check if the user's keys are cached
            CrateKeys keys = this.userKeys.getOrDefault(player, new CrateKeys());
            if (!keys.getContent().isEmpty()) return keys;

            // Get the user's keys from the database
            this.databaseConnector.connect(connection -> {
                try (PreparedStatement statement = connection.prepareStatement("SELECT `items` FROM " + this.getTablePrefix() + "keys WHERE `player` = ?")) {
                    statement.setString(1, player.toString());
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        keys.set(this.gson.fromJson(resultSet.getString("items"), CrateKeys.class));
                        this.userKeys.put(player, keys);
                    }
                }
            });

            return keys;
        });
    }

    /**
     * Save all the locations of a crate
     *
     * @param crate The crate to save
     */
    public void save(Crate crate) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement clearLocations = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "locations WHERE `crateName` = ?")) {
                clearLocations.setString(1, crate.getId());
                clearLocations.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "locations (`crateName`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?, ?)")) {
                for (Location location : crate.getLocations()) {
                    statement.setString(1, crate.getId());
                    statement.setInt(2, location.getBlockX());
                    statement.setInt(3, location.getBlockY());
                    statement.setInt(4, location.getBlockZ());
                    statement.setString(5, location.getWorld().getName());
                    statement.addBatch();
                }

                statement.executeBatch();
            }
        }));
    }

    /**
     * Get all the locations of a crate
     *
     * @param crate The crate to get the locations for
     */
    public CompletableFuture<List<Location>> getLocations(Crate crate) {
        return CompletableFuture.supplyAsync(() -> {
            List<Location> locations = new ArrayList<>();

            this.databaseConnector.connect(connection -> {
                try (PreparedStatement statement = connection.prepareStatement("SELECT `x`, `y`, `z`, `world` FROM " + this.getTablePrefix() + "locations WHERE `crateName` = ?")) {
                    statement.setString(1, crate.getId());
                    locations.addAll(this.construct(statement.executeQuery()));
                }
            });

            return locations;
        });
    }

    private List<Location> construct(ResultSet resultSet) throws SQLException {
        List<Location> result = new ArrayList<>();
        while (resultSet.next()) {
            int x = resultSet.getInt("x");
            int y = resultSet.getInt("y");
            int z = resultSet.getInt("z");
            String world = resultSet.getString("world");

            result.add(new Location(Bukkit.getWorld(world), x, y, z));
        }

        return result;
    }

    /**
     * Decompress a byte array into a list of ItemStacks
     *
     * @param data The byte array
     * @return The list of items.
     */
    public List<ItemStack> decompressItems(byte[] data) {
        final List<ItemStack> items = new ArrayList<>();
        try (
                ByteArrayInputStream is = new ByteArrayInputStream(data);
                BukkitObjectInputStream ois = new BukkitObjectInputStream(is)
        ) {

            int amount = ois.readInt();
            for (int i = 0; i < amount; i++)
                items.add((ItemStack) ois.readObject());

        } catch (IOException | ClassNotFoundException ignored) {
        }

        return items;
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(_1_CreateInitialTables.class);
    }

    /**
     * Execute a runnable async off the main thread
     *
     * @param runnable The runnable to run async
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
