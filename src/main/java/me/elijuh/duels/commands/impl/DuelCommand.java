package me.elijuh.duels.commands.impl;

import com.google.common.collect.ImmutableList;
import me.elijuh.duels.Core;
import me.elijuh.duels.commands.Command;
import me.elijuh.duels.objects.Kit;
import me.elijuh.duels.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelCommand extends Command {
    public DuelCommand() {
        super("duel", ImmutableList.of(), null);
        setUsage(Text.color("&cUsage: &7/duel <player> [kit]"));
    }


    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null || !p.canSee(target)) {
                p.sendMessage(Text.color("&cPlayer is not online."));
                return;
            }
            if (target == p) {
                p.sendMessage(Text.color("&cYou can't duel yourself."));
                return;
            }
            Kit kit = args.length > 1 ? Core.i().getKitManager().getKit(args[1]) : Core.i().getKitManager().getDefaultKit();
            if (kit == null) {
                p.sendMessage(Text.color("&cKit doesn't exist."));
                return;
            }
            Core.i().getDuelsManager().createInvite(kit, p, target);
        } else {
            p.sendMessage(getUsage());
        }
    }
}
