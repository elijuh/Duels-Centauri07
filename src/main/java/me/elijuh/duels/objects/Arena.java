package me.elijuh.duels.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor
public class Arena {
    private static int lastId = 0;

    private final int id = lastId++;
    private final String name;
    private final Location loc1, loc2;
    @Setter
    private boolean active = false;
}
