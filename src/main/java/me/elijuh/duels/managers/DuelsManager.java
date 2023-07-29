package me.elijuh.duels.managers;

import lombok.Getter;
import me.elijuh.duels.Core;
import me.elijuh.duels.objects.Duel;
import me.elijuh.duels.objects.DuelInvite;
import me.elijuh.duels.objects.DuelState;
import me.elijuh.duels.objects.Kit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class DuelsManager implements Listener {
    private final Set<Duel> duels = new HashSet<>();
    private final Map<Player, Map<UUID, DuelInvite>> invites = new HashMap<>();
    private final Map<Player, Duel> playerIndex = new HashMap<>();

    public DuelsManager() {
        Bukkit.getPluginManager().registerEvents(this, Core.i());
    }

    public void createDuel(Kit kit, Player player1, Player player2) {
        Duel duel = new Duel(kit, player1, player2);
        if (duel.getArena() == null) {
            duel.announce("&7No arenas available, duel cancelled.");
            return;
        }
        Map<UUID, DuelInvite> remove = this.invites.remove(player1);
        if (remove != null) {
            remove.values().forEach(BukkitRunnable::cancel);
        }
        remove = this.invites.remove(player2);
        if (remove != null) {
            remove.values().forEach(BukkitRunnable::cancel);
        }
        playerIndex.put(player1, duel);
        playerIndex.put(player2, duel);
        duel.start();
        duels.add(duel);
    }

    public void createInvite(Kit kit, Player p, Player target) {
        invites.computeIfAbsent(p, k -> new HashMap<>()).put(target.getUniqueId(), new DuelInvite(kit, p, target));
    }

    public void delete(Duel duel) {
        if (duel.getPlayer1().isOnline()) {
            duel.getData1().restore();
        }
        if (duel.getPlayer2().isOnline()) {
            duel.getData2().restore();
        }
        duel.getArena().setActive(false);
        playerIndex.remove(duel.getPlayer1());
        playerIndex.remove(duel.getPlayer2());
        duels.remove(duel);
    }

    public void shutdown() {
        for (Duel duel : new HashSet<>(duels)) {
            delete(duel);
        }
    }

    @EventHandler
    public void on(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        Duel duel = playerIndex.get(p);
        if (duel != null && duel.getState() != DuelState.ACTIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        if (playerIndex.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Map<UUID, DuelInvite> remove = invites.remove(e.getPlayer());
        if (remove != null) {
            remove.values().forEach(BukkitRunnable::cancel);
        }

        Duel duel = playerIndex.get(e.getPlayer());
        if (duel != null) {
            duel.eliminate(e.getPlayer());
        }
    }

    @EventHandler
    public void on(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player p && playerIndex.containsKey(p)) {
            e.getDrops().clear();
        }
    }

    @EventHandler
    public void on(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Duel duel = playerIndex.get(p);
        if (duel != null) {
            duel.eliminate(p);
            Location loc = p.getLocation();
            p.setGameMode(GameMode.SPECTATOR);
            e.setDeathMessage(null);
            Bukkit.getScheduler().runTask(Core.i(), ()-> {
                p.spigot().respawn();
                p.teleport(loc);
            });
        }
    }
}
