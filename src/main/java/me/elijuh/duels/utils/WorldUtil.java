package me.elijuh.duels.utils;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@UtilityClass
public class WorldUtil {

    public void writeLocation(ConfigurationSection section, Location location) {
        section.set("world", location.getWorld());
        section.set("x", location.getBlockX());
        section.set("y", location.getBlockY());
        section.set("z", location.getBlockZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    public Location readLocation(ConfigurationSection section) throws IllegalArgumentException {
        Preconditions.checkArgument(section.contains("x"), "location must have x value");
        Preconditions.checkArgument(section.contains("y"), "location must have y value");
        Preconditions.checkArgument(section.contains("z"), "location must have z value");
        String name = section.getString("world");
        World world = name != null ? Bukkit.getWorld(name) : null;
        return new Location(world,
                section.getInt("x") + 0.5,
                section.getInt("y"),
                section.getInt("z") + 0.5,
                Double.valueOf(section.getDouble("yaw", 0)).floatValue(),
                Double.valueOf(section.getDouble("pitch", 0)).floatValue()
        );
    }

    public void resetPlayer(Player p) {
        p.getInventory().clear();
        p.setHealth(20);
        p.setFoodLevel(40);
        p.setFireTicks(0);
        p.setExp(0);
        p.setLevel(0);
        p.setAllowFlight(false);
        p.setGameMode(GameMode.SURVIVAL);
        p.setWalkSpeed(0.2f);
        p.setArrowsInBody(0);
        p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
    }
}
