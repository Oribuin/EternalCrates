package xyz.oribuin.eternalcrates.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_CreateInitialTables extends DataMigration {

    public _1_CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {

        // Create the required tables unclaimed keys.
        final String itemsQuery = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "keys (player VARCHAR(36), items TEXT, PRIMARY KEY(player))";
        final String crateQuery = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "locations (crateName VARCHAR(36), x INT, y INT, z INT, world VARCHAR(50))";

        try (Statement statement = connection.createStatement()) {
            statement.addBatch(itemsQuery);
            statement.addBatch(crateQuery);
            statement.executeBatch();
        }
    }

}
