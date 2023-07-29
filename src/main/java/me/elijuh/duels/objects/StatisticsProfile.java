package me.elijuh.duels.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class StatisticsProfile {
    private final UUID uuid;
    @Setter
    private String name;
    private final Map<DuelsStatistic, Integer> statistics = new HashMap<>();

    public void put(DuelsStatistic statistic, int i) {
        statistics.put(statistic, i);
    }

    public int get(DuelsStatistic statistic) {
        return statistics.getOrDefault(statistic, 0);
    }

    public int get(DuelsStatistic statistic, int def) {
        return statistics.getOrDefault(statistic, def);
    }

    public void increment(DuelsStatistic statistic) {
        put(statistic, get(statistic) + 1);
    }
}
