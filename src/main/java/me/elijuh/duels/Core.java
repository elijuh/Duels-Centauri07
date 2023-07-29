package me.elijuh.duels;

import lombok.Getter;
import me.elijuh.duels.commands.CommandManager;
import me.elijuh.duels.commands.impl.AcceptCommand;
import me.elijuh.duels.commands.impl.DuelCommand;
import me.elijuh.duels.commands.impl.StatsCommand;
import me.elijuh.duels.managers.ArenaManager;
import me.elijuh.duels.managers.DuelsManager;
import me.elijuh.duels.managers.KitManager;
import me.elijuh.duels.managers.UserManager;
import me.elijuh.duels.storage.storages.MySQLStorage;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Core extends JavaPlugin {
    private static Core instance;
    private MySQLStorage storage;

    private UserManager userManager;
    private ArenaManager arenaManager;
    private DuelsManager duelsManager;
    private KitManager kitManager;
    private CommandManager commandManager;

    @Override
    public void onLoad() {
        instance = this;
        storage = new MySQLStorage();
    }

    @Override
    public void onEnable() {
        userManager = new UserManager();
        arenaManager = new ArenaManager();
        duelsManager = new DuelsManager();
        kitManager = new KitManager();
        commandManager = new CommandManager();

        commandManager.register(new DuelCommand());
        commandManager.register(new AcceptCommand());
        commandManager.register(new StatsCommand());
    }

    @Override
    public void onDisable() {
        commandManager.unregisterAll();
        userManager.shutdown();
        duelsManager.shutdown();
        storage.shutdown();
    }

    public static Core i() {
        return instance;
    }
}
