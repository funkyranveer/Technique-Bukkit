package scha.efer.technique.profile.stats.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.stats.menu.ProfileMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"stats", "profile", "player", "elo"})
public class StatsCommand {

    public void execute(Player player) {
        new ProfileMenu(player).openMenu(player);
    }

    public void execute(Player player, @CPL("player") Player target) {
        if (target == null)
            return;

        new ProfileMenu(target).openMenu(player);
    }

}
