package scha.efer.technique.event.impl.tournament.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.impl.tournament.Tournament;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "tournament cancel", permission = "tournament.cancel")
public class TournamentCancelCommand {

    public void execute(Player player) {
        if (Tournament.CURRENT_TOURNAMENT == null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "There isn't an active Tournament right now");
            return;
        }
        if (Tournament.RUNNABLE != null) {
            Tournament.RUNNABLE.cancel();
        }
        Tournament.CURRENT_TOURNAMENT.cancel();
        Tournament.CURRENT_TOURNAMENT = null;
        player.sendMessage(ChatColor.DARK_PURPLE + "The Tournament has been cancelled");
    }
}


