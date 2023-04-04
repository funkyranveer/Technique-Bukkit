package scha.efer.technique.event.impl.tournament.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.impl.tournament.Tournament;
import scha.efer.technique.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "tournament")
public class TournamentCommand {

    public void execute(Player player) {
        if (Tournament.CURRENT_TOURNAMENT != null) {
            Tournament tournament = Tournament.CURRENT_TOURNAMENT;
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.DARK_PURPLE).append("Tournament ").append(tournament.getTeamCount()).append("v").append(tournament.getTeamCount()).append("'s matches:");
            builder.append(ChatColor.DARK_PURPLE).append(" ").append(ChatColor.DARK_PURPLE).append("\n");
            builder.append(CC.RED).append("Ladder: ").append(ChatColor.WHITE).append(tournament.getLadder().getName()).append("\n");
            builder.append(ChatColor.DARK_PURPLE).append(" ").append(ChatColor.DARK_PURPLE).append("\n");
            if(tournament.getTournamentMatches().size() > 0) {
                for (Tournament.TournamentMatch match : tournament.getTournamentMatches()) {
                    String teamANames = match.getTeamA().getLeader().getPlayer().getName();
                    String teamBNames = match.getTeamB().getLeader().getPlayer().getName();
                    builder.append(ChatColor.DARK_PURPLE).append(teamANames).append("'s Party").append(ChatColor.WHITE).append(" vs. ").append(ChatColor.DARK_PURPLE).append(teamBNames).append("'s Party").append("\n");
                }
            } else if (tournament.getSumoTournamentMatches().size() > 0) {
                for (Tournament.SumoTournamentMatch match : tournament.getSumoTournamentMatches()) {
                    String teamANames = match.getTeamA().getLeader().getPlayer().getName();
                    String teamBNames = match.getTeamB().getLeader().getPlayer().getName();
                    builder.append(ChatColor.DARK_PURPLE).append(teamANames).append("'s Party").append(ChatColor.WHITE).append(" vs. ").append(ChatColor.DARK_PURPLE).append(teamBNames).append("'s Party").append("\n");
                }
            }

            builder.append(ChatColor.DARK_PURPLE).append(" ").append(ChatColor.DARK_PURPLE).append("\n");
            builder.append(CC.RED).append("Round: ").append(ChatColor.WHITE).append(tournament.getRound());
            builder.append("\n");
            builder.append(CC.RED).append("Players: ").append(ChatColor.WHITE).append(tournament.getParticipatingCount()).append("\n");
            player.sendMessage(builder.toString());
        } else {
            player.sendMessage(ChatColor.DARK_PURPLE + "There aren't any active Tournaments");
        }
    }
}


