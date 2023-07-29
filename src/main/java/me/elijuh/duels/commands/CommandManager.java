package me.elijuh.duels.commands;

import lombok.Getter;
import me.elijuh.duels.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class CommandManager {
    private final List<Command> commands = new ArrayList<>();
    private final CommandMap map;
    private final JavaPlugin plugin = Core.i();

    public CommandManager() {
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            map = (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get CommandMap.");
        }
    }

    public void register(Command... commands) {
        for (Command command : commands) {
            try {
                unregister(command.getName());
                for (String s : command.getAliases()) {
                    unregister(s);
                }
                map.register(plugin.getName(), command);
                this.commands.add(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void unregister(String name) {
        Map<String, Command> known;
        try {
            Method method = map.getClass().getDeclaredMethod("getKnownCommands");
            known = (Map<String, Command>) method.invoke(map);
        } catch (Exception e) {
            try {
                Field field = map.getClass().getDeclaredField("knownCommands");
                field.setAccessible(true);
                known = (Map<String, Command>) field.get(map);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
        known.remove(name);
        commands.removeIf(command -> command.getName().equals(name));
    }

    public void unregisterAll() {
        for (Command command : new ArrayList<>(commands)) {
            unregister(command.getName());
            command.getAliases().forEach(this::unregister);
        }
    }
}
