package me.elijuh.duels.commands.impl;

import com.google.common.collect.ImmutableList;
import me.elijuh.duels.Core;
import me.elijuh.duels.commands.Command;
import me.elijuh.duels.objects.DuelsStatistic;
import me.elijuh.duels.objects.StatisticsProfile;
import me.elijuh.duels.objects.User;
import me.elijuh.duels.utils.Text;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StatsCommand extends Command {
    public StatsCommand() {
        super("stats", ImmutableList.of(), null);
        setUsage(Text.color("&cUsage: &7/stats <name>"));
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length == 1) {
            User user = Core.i().getUserManager().getUser(args[0]);
            if (user != null) {
                p.sendMessage(format(user.getStatistics()));
            } else {
                CompletableFuture.runAsync(()-> {
                    StatisticsProfile profile = Core.i().getStorage().dao().getByName(args[0]);
                    if (profile != null) {
                        p.sendMessage(format(profile));
                    } else {
                        p.sendMessage(Text.color("&cPlayer does not exist."));
                    }
                });
            }
        } else {
            p.sendMessage(getUsage());
        }
    }

    private String format(StatisticsProfile profile) {
        return Text.color("&6&m                                  " +
                "\n&eStats for &a" + profile.getName() +
                "\n&7Wins: &f" + profile.get(DuelsStatistic.WINS) +
                "\n&7Losses: &f" + profile.get(DuelsStatistic.LOSSES) +
                "\n&7Kills: &f" + profile.get(DuelsStatistic.KILLS) +
                "\n&7Deaths: &f" + profile.get(DuelsStatistic.DEATHS) +
                "\n&7Streak: &f" + profile.get(DuelsStatistic.WINSTREAK) +
                "\n&6&m                                  "
        );
    }
}
