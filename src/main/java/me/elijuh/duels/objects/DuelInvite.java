package me.elijuh.duels.objects;

import lombok.Getter;
import me.elijuh.duels.Core;
import me.elijuh.duels.utils.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DuelInvite extends BukkitRunnable {
    private final Player inviter, invited;
    @Getter
    private final Kit kit;
    private int timer;

    public DuelInvite(Kit kit, Player inviter, Player invited) {
        this.kit = kit;
        this.inviter = inviter;
        this.invited = invited;
        TextComponent comp = new TextComponent(Text.color("\n&e" + inviter.getName() + " &ahas invited you to a duel!" +
                "\n&7Kit: &f" + kit.getName() +
                "\n&a(Click to accept)"
        ));
        comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + inviter.getName()));
        invited.spigot().sendMessage(comp);
        invited.playSound(invited.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        inviter.sendMessage(Text.color("&aYou have invited &e" + invited.getName() + " &ato a duel with kit &e" + kit.getName() + "&a!"));

        runTaskTimerAsynchronously(Core.i(), 20L, 20L);
    }

    @Override
    public void run() {
        if (++timer == 60) {
            cancel();
            if (invited.isOnline()) {
                invited.sendMessage(Text.color("&cThe invite from &c" + inviter.getName() + " &chas expired."));
            }
        }
    }
}
