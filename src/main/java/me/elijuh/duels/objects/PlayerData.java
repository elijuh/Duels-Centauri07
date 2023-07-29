package me.elijuh.duels.objects;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class PlayerData {
    private final Player player;
    private final ItemStack[] contents;
    private final ItemStack[] armorContents;
    private final GameMode gameMode;
    private final double health;
    private final int food, level;
    private final float exp;
    private final boolean allowFlight, isFlying;
    private final Location location;
    private final Collection<PotionEffect> potionEffects;

    public PlayerData(Player player) {
        this.player = player;
        this.contents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.gameMode = player.getGameMode();
        this.health = player.getHealth();
        this.food = player.getFoodLevel();
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.allowFlight = player.getAllowFlight();
        this.isFlying = player.isFlying();
        this.location = player.getLocation();
        this.potionEffects = player.getActivePotionEffects();
    }

    public void restore() {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armorContents);
        player.setGameMode(gameMode);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setLevel(level);
        player.setExp(exp);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : potionEffects) {
            player.addPotionEffect(effect);
        }
        player.setAllowFlight(allowFlight);
        player.setFlying(isFlying);
        player.setFlying(allowFlight);
        player.teleport(location);
    }
}
