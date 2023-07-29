package me.elijuh.duels.storage.storages;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.elijuh.duels.Core;
import me.elijuh.duels.storage.Dao;
import me.elijuh.duels.storage.configuration.MySQLConfiguration;
import me.elijuh.duels.storage.dao.MySQLDao;
import me.elijuh.duels.utils.SchemaReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MySQLStorage {
    @Getter
    private final HikariDataSource dataSource;

    private final Dao dao;

    public MySQLStorage() {
        dataSource = new HikariDataSource(MySQLConfiguration.asHikariConfig());
        dao = new MySQLDao(this);

        String path = "schema.sql";
        try (InputStream inputStream = Core.i().getResource(path)) {
            if (inputStream == null) {
                throw new IllegalStateException("Could not find " + path + "!");
            }
            try (Connection connection = connection();
                 Statement statement = connection.createStatement()) {
                List<String> queries = SchemaReader.getStatements(inputStream);
                for (String query : queries) {
                    statement.execute(query);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Dao dao() {
        return dao;
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public Connection connection() throws SQLException {
        return dataSource.getConnection();
    }
}
