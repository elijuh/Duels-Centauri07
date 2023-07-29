package me.elijuh.duels.storage.configuration;

import com.zaxxer.hikari.HikariConfig;
import me.elijuh.duels.Core;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Properties;

public class MySQLConfiguration {
    private static final YamlConfiguration yaml;
    private static final File file = new File(Core.i().getDataFolder() + File.separator + "credentials", "mysql.yml");

    static {
        yaml = YamlConfiguration.loadConfiguration(file);
        yaml.options().copyDefaults(true);
        yaml.addDefault("host", "127.0.0.1");
        yaml.addDefault("port", 3306);
        yaml.addDefault("database", "duels");
        yaml.addDefault("user", "root");
        yaml.addDefault("password", "");
        yaml.addDefault("pool.maximum-pool-size", 10);
        yaml.addDefault("pool.minimum-idle", 10);
        yaml.addDefault("pool.maximum-lifetime", 180000);
        yaml.addDefault("pool.connection-timeout", 5000);
        yaml.addDefault("properties.useUnicode", true);
        yaml.addDefault("properties.characterEncoding", "utf8");
        try {
            yaml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HikariConfig asHikariConfig() {
        HikariConfig config = new HikariConfig();
        String host = yaml.getString("host");
        int port = yaml.getInt("port");
        String database = yaml.getString("database");

        config.setPoolName("duels-pool");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(yaml.getString("user"));
        config.setPassword(yaml.getString("password"));
        config.setMaximumPoolSize(yaml.getInt("pool.maximum-pool-size"));
        config.setMinimumIdle(yaml.getInt("pool.minimum-idle"));
        config.setMaxLifetime(yaml.getInt("pool.maximum-lifetime"));
        config.setConnectionTimeout(yaml.getInt("pool.connection-timeout"));
        ConfigurationSection section = yaml.getConfigurationSection("properties");
        if (section != null) {
            Properties properties = new Properties();
            for (String key : section.getKeys(false)) {
                properties.setProperty(key, section.getString(key));
            }
            config.setDataSourceProperties(properties);
        }
        return config;
    }
}
