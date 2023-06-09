package scha.efer.technique.event.impl.tournament.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.impl.tournament.Tournament;
import scha.efer.technique.party.Party;
import scha.efer.technique.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "tournament join")
public class TournamentJoinCommand {

    public void execute(Player player) {
        if (Tournament.CURRENT_TOURNAMENT == null || Tournament.CURRENT_TOURNAMENT.hasStarted()) {
            player.sendMessage(ChatColor.DARK_PURPLE + "There isn't a joinable Tournament");
            return;
        }
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (Tournament.CURRENT_TOURNAMENT.getTeamCount() == 1) {
            Party party = Profile.getByUuid(player.getUniqueId()).getParty();
            if (party != null && party.getPlayers().size() != 1) {
                player.sendMessage("This is a solo Tournament");
                return;
            }
        } else {
            Party party = Profile.getByUuid(player.getUniqueId()).getParty();
            if (party == null || party.getPlayers().size() != Tournament.CURRENT_TOURNAMENT.getTeamCount()) {
                player.sendMessage(ChatColor.DARK_PURPLE + "The Tournament needs " + Tournament.CURRENT_TOURNAMENT.getTeamCount() + " players to start.");
                return;
            }
            if (!party.isLeader(player.getUniqueId())) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Only Leaders can do this");
                return;
            }
        }
        if (profile.isBusy(player)) {
            player.sendMessage(ChatColor.DARK_PURPLE + "You cannot join the Tournament in your current state");
            return;
        }
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            player.chat("/party create");
            party = Profile.getByUuid(player.getUniqueId()).getParty();
        }
        Tournament.CURRENT_TOURNAMENT.participate(party);
    }
}


