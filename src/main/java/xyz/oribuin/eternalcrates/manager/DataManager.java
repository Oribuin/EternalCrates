package xyz.oribuin.eternalcrates.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.CrateKeys;
import xyz.oribuin.eternalcrates.database.migration._1_CreateInitialTables;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
            try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO " + this.getTablePrefix() + "keys (`player`, `items`) VALUES (?, ?)")) {
                statement.setString(1, player.toString());
                statement.setString(2, this.gson.toJson(keys));
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
        CrateKeys crateKeys = this.userKeys.get(player);
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
        if (this.userKeys.get(player) != null)
            return this.userKeys.get(player);

        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `items` FROM " + this.getTablePrefix() + "keys WHERE `player` = ?")) {
                statement.setString(1, player.toString());
                ResultSet resultSet = statement.executeQuery();
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
            try (PreparedStatement statement = connection.prepareStatement("SELECT `x`, `y`, `z`, `world` FROM " + this.getTablePrefix() + "locations WHERE `crateName` = ?")) {
                statement.setString(1, crate.getId());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    World world = Bukkit.getWorld(resultSet.getString("world"));
                    if (world == null)
                        continue;

                    crate.getLocations().add(new Location(world, x, y, z));
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
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "locations WHERE `crateName` = ?")) {
                statement.setString(1, crate.getId());
                statement.executeUpdate();
            }

            for (Location loc : crate.getLocations()) {
                try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO " + this.getTablePrefix() + "locations (`crateName`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)")) {
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
     * Drop the data migration table to reset the database migrations
     * We don't want to have to create the old tables again just to drop them
     */
    public void dropTableMigration() {
        this.async(() -> this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DROP TABLE " + this.getTablePrefix() + "migrations")) {
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Recreate all the location data for the crates
     */
    public void recreateLocations() {
        this.async(() -> this.databaseConnector.connect(connection -> {
            Map<String, Location> locations = new HashMap<>();

            String loadOldLocations = "SELECT `crate`, `x`, `y`, `z`, `world` FROM " + this.getTablePrefix() + "crates";
            try (PreparedStatement statement = connection.prepareStatement(loadOldLocations)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Location loc = new Location(
                            Bukkit.getWorld(resultSet.getString("world")),
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z")
                    );

                    locations.put(resultSet.getString("crate"), loc);
                }
            }

            String dropOldLocations = "DROP TABLE " + this.getTablePrefix() + "crates";
            try (PreparedStatement statement = connection.prepareStatement(dropOldLocations)) {
                statement.executeUpdate();
            }

            for (Map.Entry<String, Location> entry : locations.entrySet()) {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "locations (`crateName`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)")) {
                    statement.setString(1, entry.getKey());
                    statement.setDouble(2, entry.getValue().getX());
                    statement.setDouble(3, entry.getValue().getY());
                    statement.setDouble(4, entry.getValue().getZ());
                    statement.setString(5, entry.getValue().getWorld().getName());
                    statement.executeUpdate();
                }
            }
        }));
    }

    /**
     * Recreate all the key data for the users
     */
    public void recreateKeys() {
        this.async(() -> this.databaseConnector.connect(connection -> {
            Map<UUID, CrateKeys> keys = new HashMap<>();

            String loadVirtualKeys = "SELECT `player`, `keys` FROM " + this.getTablePrefix() + "virtual";

            // Load all the virtual keys
            try (PreparedStatement statement = connection.prepareStatement(loadVirtualKeys)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    CrateKeys crateKeys = gson.fromJson(resultSet.getString("keys"), CrateKeys.class);
                    keys.put(UUID.fromString(resultSet.getString("player")), crateKeys);
                }
            }

            // Drop the old virtual keys table
            String dropVirtualKeys = "DROP TABLE " + this.getTablePrefix() + "virtual";
            try (PreparedStatement statement = connection.prepareStatement(dropVirtualKeys)) {
                statement.executeUpdate();
            }


            // Load all the unclaimed keys
            Map<UUID, List<ItemStack>> items = new HashMap<>();
            String loadUnclaimedKeys = "SELECT `player`, `items` FROM " + this.getTablePrefix() + "items";

            try (PreparedStatement statement = connection.prepareStatement(loadUnclaimedKeys)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    UUID player = UUID.fromString(resultSet.getString("player"));
                    List<ItemStack> itemsStacks = decompressItems(resultSet.getBytes("items"));
                    items.put(player, itemsStacks);
                }
            }

            // Convert itemstacks to crate keys
            // Convert the unclaimed keys to virtual crate keys
            for (Map.Entry<UUID, List<ItemStack>> entry : items.entrySet()) {

                CrateKeys newCrateKeys = keys.getOrDefault(entry.getKey(), new CrateKeys());
                for (ItemStack item : entry.getValue()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) continue;

                    String crateId = meta.getPersistentDataContainer().get(new NamespacedKey(this.rosePlugin, "crateKey"), PersistentDataType.STRING);
                    if (crateId == null) continue;

                    // Add the crate key the users virtual keys
                    int currentCount = newCrateKeys.getKeys().getOrDefault(crateId, 0);
                    newCrateKeys.getKeys().put(crateId, currentCount + 1);
                }

                keys.put(entry.getKey(), newCrateKeys);
            }

            // Drop the old table
            String dropUnclaimedKeys = "DROP TABLE " + this.getTablePrefix() + "items";
            try (PreparedStatement statement = connection.prepareStatement(dropUnclaimedKeys)) {
                statement.executeUpdate();
            }

            // Save the new keys
            for (Map.Entry<UUID, CrateKeys> entry : keys.entrySet()) {
                this.userKeys.put(entry.getKey(), entry.getValue());

                try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO " + this.getTablePrefix() + "keys (`player`, `items`) VALUES (?, ?)")) {
                    statement.setString(2, entry.getKey().toString());
                    statement.setString(1, this.gson.toJson(entry.getValue()));
                    statement.executeUpdate();
                }
            }
        }));

    }

    /**
     * Add keys to the users virtual keys
     *
     * @param user   The user to add the keys to
     * @param crate  The crate key to add
     * @param amount The amount of keys to add
     */
    public void addKeys(UUID user, String crate, int amount) {
        Map<String, Integer> keys = this.userKeys.getOrDefault(user, new CrateKeys()).getKeys();
        int current = keys.getOrDefault(crate, 0);

        keys.put(crate, current + amount);
        this.saveUser(user, new CrateKeys(keys));
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
