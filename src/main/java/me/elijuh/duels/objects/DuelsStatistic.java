package me.elijuh.duels.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DuelsStatistic {
    WINS("wins"),
    LOSSES("losses"),
    KILLS("kills"),
    DEATHS("deaths"),
    WINSTREAK("streak");

    private final String key;
}
