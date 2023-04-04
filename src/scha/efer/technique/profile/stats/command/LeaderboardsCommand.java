package scha.efer.technique.profile.stats.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.stats.menu.LeaderboardsMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"leaderboards", "lb"})
public class LeaderboardsCommand {

    public void execute(Player player) {
        new LeaderboardsMenu().openMenu(player);
    }

}
