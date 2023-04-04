package scha.efer.technique.event.impl.tournament.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.impl.tournament.Tournament;
import scha.efer.technique.party.Party;
import scha.efer.technique.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "tournament leave")
public class TournamentLeaveCommand {

    public void execute(Player player) {
        if (Tournament.CURRENT_TOURNAMENT == null || Tournament.CURRENT_TOURNAMENT.hasStarted()) {
            player.sendMessage(ChatColor.DARK_PURPLE + "There isn't a Tournament you can leave");
            return;
        }
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            player.sendMessage("You aren't currently in a Tournament");
            return;
        }
        if (!Tournament.CURRENT_TOURNAMENT.isParticipating(player)) {
            player.sendMessage("You aren't currently in a Tournament");
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.DARK_PURPLE + "&5Only Leaders can do this");
            return;
        }
        Tournament.CURRENT_TOURNAMENT.leave(party);
    }
}


