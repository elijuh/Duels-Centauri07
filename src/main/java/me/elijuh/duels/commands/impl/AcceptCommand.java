package me.elijuh.duels.commands.impl;

import com.google.common.collect.ImmutableList;
import me.elijuh.duels.Core;
import me.elijuh.duels.commands.Command;
import me.elijuh.duels.objects.DuelInvite;
import me.elijuh.duels.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AcceptCommand extends Command {
    public AcceptCommand() {
        super("accept", ImmutableList.of(), null);
        setUsage(Text.color("&cUsage: &7/accept <name>"));
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                p.sendMessage(Text.color("&cPlayer is not online."));
                return;
            }
            Map<UUID, DuelInvite> map = Core.i().getDuelsManager().getInvites().get(target);
            if (map != null) {
                DuelInvite invite = map.get(p.getUniqueId());
                if (invite != null) {
                    Core.i().getDuelsManager().createDuel(invite.getKit(), target, p);
                    invite.cancel();
                    return;
                }
            }
            p.sendMessage(Text.color("&cYou don't have an invite from that player."));
        } else {
            p.sendMessage(getUsage());
        }
    }
}
