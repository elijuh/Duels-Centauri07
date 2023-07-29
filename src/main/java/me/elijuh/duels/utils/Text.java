package me.elijuh.duels.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class Text {

    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        return String.format("%02d:%02d", minutes, seconds % 60);
    }
}
