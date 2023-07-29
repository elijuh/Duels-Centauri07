package me.elijuh.duels.objects;

import lombok.Getter;
import me.elijuh.duels.Core;

import java.util.UUID;

@Getter
public class User {
    private final UUID uuid;
    private final String name;
    private final StatisticsProfile statistics;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        StatisticsProfile statistics = Core.i().getStorage().dao().get(uuid);
        if (statistics != null) {
            statistics.setName(name);
            this.statistics = statistics;
        } else {
            this.statistics = new StatisticsProfile(uuid, name);
        }
    }
}
