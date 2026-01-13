package db;

import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Db {
    public static final String DATABASE_NAME = "asese";
    public static final String DATABASE_CONNECTION_STRING = "jdbc:mysql://localserver785.mooo.com:3306/" + DATABASE_NAME;
    public static final String DATABASE_USER = "root";
    public static final String DATABASE_PASSWORD = "asese";

    private static Connection connection;

    /**
     * Returns the connection instance to the database, which will be close automatically on program shutdown.
     * The connection is validated beforehand with a simple SELECT 1 query.
     * @return Connection
     */
    public static Connection getConnection() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(DATABASE_CONNECTION_STRING, DATABASE_USER, DATABASE_PASSWORD);
                if (!connection.isValid(1000)) {
                    throw new DatabaseException("Database connection timeout exceeded, the connection is not stable enough");
                }

                testQuery();
                buildSchema();
                // insertData();

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        if (!connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        throw new DatabaseException("Failed to close connection with the database", e);
                    }
                }));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not establish a connection with the database", e);
        }

        return connection;
    }

    /**
     * Query used to test connection to database
     */
    private static void testQuery() {
        try (
                var st = connection.prepareStatement("SELECT 1");
                var rs = st.executeQuery();
        ) {
            if (!rs.next()) {
                throw new DatabaseException("Database connection test failed, expected at least one row");
            }
            if (rs.getInt(1) != 1) {
                throw new DatabaseException("Database connection test failed, expected '1' as return column");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection test failed", e);
        }
    }

    private static void buildSchema() {
        var schemaUrl = Db.class.getResource("/db/schema.sql");
        if (schemaUrl == null) {
            throw new RuntimeException("Could not find /db/schema.sql in resources");
        }
        var encodedResource = new EncodedResource(new UrlResource(schemaUrl));
        ScriptUtils.executeSqlScript(
                connection,
                encodedResource,
                false,
                false,
                "--",
                ";",
                "/*",
                "*/"
        );

        System.out.println("[INFO]: Database build successfully");
    }


    private static void insertData() {
        var dataUrl = Db.class.getResource("/db/data.sql");
        if (dataUrl == null) {
            throw new RuntimeException("Could not find /db/data.sql in resources");
        }
        var encodedResource = new EncodedResource(new UrlResource(dataUrl));
        ScriptUtils.executeSqlScript(
                connection,
                encodedResource,
                false,
                false,
                "--",
                ";",
                "/*",
                "*/"
        );

        System.out.println("[INFO]: Data inserted successfully in the database");
    }
}