package me.elijuh.duels.managers;

import me.elijuh.duels.Core;
import me.elijuh.duels.objects.Arena;
import me.elijuh.duels.utils.WorldUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ArenaManager {
    private final Set<Arena> arenas = new HashSet<>();

    public ArenaManager() {
        File file = new File(Core.i().getDataFolder(), "arenas.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<String> keys = config.getKeys(false);
        if (keys.isEmpty()) {
            //load an example
            ConfigurationSection section = config.createSection("example");
            ConfigurationSection loc1 = section.createSection("location_one");
            ConfigurationSection loc2 = section.createSection("location_two");
            loc1.set("world", "world");
            loc1.set("x", 0);
            loc1.set("y", 0);
            loc1.set("z", 0);
            loc1.set("yaw", 0);
            loc1.set("pitch", 0);
            loc2.set("world", "world");
            loc2.set("x", 0);
            loc2.set("y", 0);
            loc2.set("z", 0);
            loc2.set("yaw", 0);
            loc2.set("pitch", 0);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (String key : keys) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;
            try {
                ConfigurationSection loc1 = section.getConfigurationSection("location_one");
                ConfigurationSection loc2 = section.getConfigurationSection("location_two");
                if (loc1 == null || loc2 == null) {
                    throw new IllegalArgumentException("both locations must exist.");
                }
                arenas.add(new Arena(key, WorldUtil.readLocation(loc1), WorldUtil.readLocation(loc2)));
            } catch (IllegalArgumentException e) {
                Core.i().getLogger().warning("Failed to load arena " + key + ": " + e.getMessage());
            }
        }
    }

    public Arena firstAvailable() {
        for (Arena arena : arenas) {
            if (!arena.isActive()) {
                return arena;
            }
        }
        return null;
    }
}
