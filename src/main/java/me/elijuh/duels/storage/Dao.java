package me.elijuh.duels.storage;

import me.elijuh.duels.objects.StatisticsProfile;

import java.util.UUID;

public interface Dao {

    void save(StatisticsProfile entry);

    StatisticsProfile get(UUID uuid);

    StatisticsProfile getByName(String name);
}
