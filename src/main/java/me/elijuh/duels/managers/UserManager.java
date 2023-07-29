package me.elijuh.duels.managers;

import me.elijuh.duels.Core;
import me.elijuh.duels.objects.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager implements Listener {
    private final Map<UUID, User> users = new HashMap<>();

    public UserManager() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            users.put(p.getUniqueId(), new User(p.getUniqueId(), p.getName()));
        }

        Bukkit.getPluginManager().registerEvents(this, Core.i());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        users.put(e.getUniqueId(), new User(e.getUniqueId(), e.getName()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent e) {
        User user = users.get(e.getPlayer().getUniqueId());
        if (user != null) {
            Core.i().getStorage().dao().save(user.getStatistics());
        }
    }

    public User getUser(String name) {
        for (User user : users.values()) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    public User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public void shutdown() {
        users.values().forEach(user -> Core.i().getStorage().dao().save(user.getStatistics()));
        users.clear();
    }
}
