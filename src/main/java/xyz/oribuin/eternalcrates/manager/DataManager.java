package xyz.oribuin.eternalcrates.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.VirtualKeys;
import xyz.oribuin.eternalcrates.database.migration._1_CreateInitialTables;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataManager extends AbstractDataManager {

    private final Map<UUID, List<ItemStack>> unclaimedKeys = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> virtualKeys = new HashMap<>();
    private final Gson gson = new Gson();

    public DataManager(RosePlugin plugin) {
        super(plugin);
    }

    /**
     * Save the user's keys to the database
     *
     * @param player The player to save the keys for
     * @param items  The items to save
     */
    public void saveUnclaimedKeys(UUID player, List<ItemStack> items) {
        this.unclaimedKeys.put(player, items);

        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "items SET `items` = ? WHERE `player` = ?")) {
                statement.setBytes(1, this.serializeItems(items));
                statement.setString(2, player.toString());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Save the user's keys to the database
     *
     * @param player The player to save the keys for
     */
    public void saveUnclaimedKeys(UUID player) {
        List<ItemStack> items = this.unclaimedKeys.get(player);
        if (items == null) {
            return;
        }

        this.saveUnclaimedKeys(player, items);
    }

    /**
     * Get the user's keys from the database
     *
     * @param player The player to get the keys for
     */
    public List<ItemStack> getUnclaimedKeys(UUID player) {
        if (this.unclaimedKeys.get(player) != null) {
            return this.unclaimedKeys.get(player);
        }

        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `items` FROM " + this.getTablePrefix() + "items WHERE `player` = ?")) {
                statement.setString(1, player.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    this.unclaimedKeys.put(player, this.deserializeItems(resultSet.getBytes(1)));
                }
            }
        }));


        return this.unclaimedKeys.getOrDefault(player, new ArrayList<>());
    }

    /**
     * Save a player's virtual keys to the database
     *
     * @param player The player to save the virtual keys for
     * @param keys   The virtual keys to save
     */
    public void saveVirtualKeys(UUID player, Map<String, Integer> keys) {
        this.virtualKeys.put(player, keys);

        this.async(() -> this.databaseConnector.connect(connection -> {
            final VirtualKeys virtualKeys = new VirtualKeys(keys);

            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "virtual SET `keys` = ? WHERE `player` = ?")) {
                statement.setString(1, gson.toJson(virtualKeys));
                statement.setString(2, player.toString());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Save the user's keys to the database
     *
     * @param player The player to save the keys for
     */
    public void saveVirtualKeys(UUID player) {
        Map<String, Integer> items = this.virtualKeys.get(player);
        if (items == null) {
            return;
        }

        this.saveVirtualKeys(player, items);
    }


    /**
     * Get a player's virtual keys from the database
     *
     * @param player The player to get the virtual keys for
     * @return The virtual keys
     */
    public Map<String, Integer> getUsersVirtualKeys(UUID player) {
        if (this.virtualKeys.get(player) != null) {
            return this.virtualKeys.get(player);
        }

        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `keys` FROM " + this.getTablePrefix() + "virtual WHERE `player` = ?")) {
                statement.setString(1, player.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final VirtualKeys virtualKeys = gson.fromJson(resultSet.getString(1), VirtualKeys.class);
                    this.virtualKeys.put(player, virtualKeys.keys());
                }
            }
        }));

        return this.virtualKeys.getOrDefault(player, new HashMap<>());
    }

    /**
     * Load the crate locations from the database
     *
     * @param crate The crate to load the locations for
     */
    public void loadCrateLocation(Crate crate) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `x`, `y`, `z`, `world` FROM " + this.getTablePrefix() + "crates WHERE `crateName` = ?")) {
                statement.setString(1, crate.getId());
                ResultSet resultSet = statement.executeQuery();
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
    public void saveCrateLocation(Crate crate, Location location) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO " + this.getTablePrefix() + "crates (`crateName`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)")) {
                statement.setString(1, crate.getId());
                statement.setDouble(2, location.getX());
                statement.setDouble(3, location.getY());
                statement.setDouble(4, location.getZ());
                statement.setString(5, location.getWorld().getName());
                statement.executeUpdate();
            }
        }));
    }

    public void saveCrateLocations(Crate crate) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "crates WHERE `crateName` = ?")) {
                statement.setString(1, crate.getId());
                statement.executeUpdate();
            }

            for (Location loc : crate.getLocations()) {
                try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO " + this.getTablePrefix() + "crates (`crateName`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)")) {
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

    /**
     * Serialize the items to a byte array
     *
     * @param keys The items to serialize
     * @return The serialized items
     */
    public byte[] serializeItems(List<ItemStack> keys) {
        byte[] data = new byte[0];
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(os)) {
            oos.writeInt(keys.size());
            for (ItemStack key : keys) {
                oos.writeObject(key);
            }

            data = os.toByteArray();
        } catch (IOException ignored) {
        }

        return data;
    }

    /**
     * Deserialize the items from a byte array
     *
     * @param data The data to deserialize
     * @return The deserialized items
     */
    public List<ItemStack> deserializeItems(byte[] data) {
        final List<ItemStack> items = new ArrayList<>();
        try (ByteArrayInputStream is = new ByteArrayInputStream(data); BukkitObjectInputStream ois = new BukkitObjectInputStream(is)) {
            final int size = ois.readInt();
            for (int i = 0; i < size; i++) {
                items.add((ItemStack) ois.readObject());
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }

        return items;
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
     * Get all the cached unclaimed keys
     *
     * @return The cached unclaimed keys
     */
    public Map<UUID, List<ItemStack>> getUnclaimedKeys() {
        return unclaimedKeys;
    }

    /**
     * Get the cached virtual keys
     *
     * @return The cached virtual keys
     */
    public Map<UUID, Map<String, Integer>> getVirtualKeys() {
        return virtualKeys;
    }
}
