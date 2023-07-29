package me.elijuh.duels.objects;

import lombok.Getter;
import me.elijuh.duels.Core;
import me.elijuh.duels.utils.Text;
import me.elijuh.duels.utils.WorldUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class Duel {
    private final PlayerData data1, data2;
    private final Player player1, player2;
    private final Arena arena;
    private final Kit kit;
    private DuelState state = DuelState.STARTING;
    private int time = 0;

    public Duel(Kit kit, Player player1, Player player2) {
        this.kit = kit;
        this.player1 = player1;
        this.player2 = player2;
        this.data1 = new PlayerData(player1);
        this.data2 = new PlayerData(player2);
        arena = Core.i().getArenaManager().firstAvailable();
        arena.setActive(true);
    }

    public void start() {
        WorldUtil.resetPlayer(player1);
        WorldUtil.resetPlayer(player2);
        player1.teleport(arena.getLoc1());
        player2.teleport(arena.getLoc2());
        kit.apply(player1);
        kit.apply(player2);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == DuelState.STARTING) {
                    if (time < 5) {
                        announce("&7Match starting in &c" + (5 - time) + "s&7.");
                        sound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f);
                    } else {
                        state = DuelState.ACTIVE;
                        time = 0;
                        sound(Sound.BLOCK_NOTE_BLOCK_PLING, 2f);
                        announce("&aMatch has started, good luck!");
                    }
                } else if (state == DuelState.ACTIVE) {
                    BaseComponent[] bar = TextComponent.fromLegacyText(Text.color("&7Game Time: &a" + Text.formatSeconds(time) + "s"));
                    player1.spigot().sendMessage(ChatMessageType.ACTION_BAR, bar);
                    player2.spigot().sendMessage(ChatMessageType.ACTION_BAR, bar);
                } else {
                    cancel();
                    return;
                }
                time++;
            }
        }.runTaskTimer(Core.i(), 0L, 20L);
    }

    public void eliminate(Player player) {
        if (state == DuelState.ENDED) return;

        StatisticsProfile eliminated = Core.i().getUserManager().getUser(player.getUniqueId()).getStatistics();
        eliminated.increment(DuelsStatistic.DEATHS);
        eliminated.increment(DuelsStatistic.LOSSES);
        eliminated.put(DuelsStatistic.WINSTREAK, 0);

        Player winner = player == player1 ? player2 : player1;
        announce("&c" + player.getName() + " &7has been eliminted by &a" + winner.getName());
        player.getWorld().strikeLightningEffect(player.getLocation());

        StatisticsProfile killer = Core.i().getUserManager().getUser(winner.getUniqueId()).getStatistics();
        killer.increment(DuelsStatistic.KILLS);
        end(winner);
    }

    public void end(Player winner) {
        state = DuelState.ENDED;

        announce("\n&a" + winner.getName() + " &7has won the Duel!\n");
        winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 2f);
        StatisticsProfile stats = Core.i().getUserManager().getUser(winner.getUniqueId()).getStatistics();
        stats.increment(DuelsStatistic.WINS);
        stats.increment(DuelsStatistic.WINSTREAK);
        Bukkit.getScheduler().runTaskLater(Core.i(), ()-> Core.i().getDuelsManager().delete(this), 100L);
    }

    public void announce(String s) {
        s = Text.color(s);
        player1.sendMessage(s);
        player2.sendMessage(s);
    }

    public void sound(Sound sound, float pitch) {
        player1.playSound(player1.getLocation(), sound, 1f, pitch);
        player2.playSound(player2.getLocation(), sound, 1f, pitch);
    }
}
