package me.elijuh.duels.managers;

import me.elijuh.duels.Core;
import me.elijuh.duels.objects.Kit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KitManager {
    private final Map<String, Kit> kits = new HashMap<>();
    private final String defaultKit;

    public KitManager() {
        File file = new File(Core.i().getDataFolder(), "kits.yml");
        if (!file.exists()) {
            Core.i().saveResource("kits.yml", true);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String defaultKit = config.getString("default-kit");
        ConfigurationSection section = config.getConfigurationSection("kits");
        if (section == null || section.getKeys(false).isEmpty()) {
            Core.i().saveResource("kits.yml", true);
            config = YamlConfiguration.loadConfiguration(file);
            section = config.getConfigurationSection("kits");
        }
        if (section == null) {
            throw new IllegalStateException("plugin cannot function without any kits.");
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection kit = section.getConfigurationSection(key);
            if (kit == null) continue;
            kits.put(key.toLowerCase(), new Kit(key.toLowerCase(), kit));
        }

        if (kits.isEmpty()) {
            throw new IllegalStateException("plugin cannot function without any kits.");
        }

        if (defaultKit == null || getKit(defaultKit) == null) {
            Optional<Kit> first = kits.values().stream().findFirst();
            if (first.isPresent()) {
                defaultKit = first.get().getName();
            }
        }
        this.defaultKit = defaultKit;
    }

    public Kit getDefaultKit() {
        return getKit(defaultKit);
    }

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }
}
