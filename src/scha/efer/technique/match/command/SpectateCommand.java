package scha.efer.technique.match.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spectate", "spec"})
public class SpectateCommand {

    public void execute(Player player, Player target) {
        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot spectate whilst frozen.");
            return;
        }

        if (target == null) {
            player.sendMessage(CC.RED + "A player with that name could not be found.");
            return;
        }

        Profile playerProfile = Profile.getByUuid(player.getUniqueId());

        if (playerProfile.isBusy(player)) {
            if (playerProfile.isFollowMode()) {
                if (!target.getName().equalsIgnoreCase(playerProfile.getFollowing().getName())) {
                    player.sendMessage(CC.RED + "You must be in the lobby to spectate.");
                    return;
                }
            } else {
                player.sendMessage(CC.RED + "You must be in the lobby and not queueing to spectate.");
                return;
            }
        }

        if (playerProfile.getParty() != null) {
            player.sendMessage(CC.RED + "You must leave your party to spectate a match.");
            return;
        }

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (!targetProfile.getOptions().isAllowSpectators() && !player.hasPermission("technique.staff")) {
            player.sendMessage(CC.RED + "That player is not allowing spectators.");
            return;
        }

        if (targetProfile.getMatch() != null) {
            for (TeamPlayer teamPlayer : targetProfile.getMatch().getTeamPlayers()) {
                Player inMatchPlayer = teamPlayer.getPlayer();
                if (inMatchPlayer != null) {
                    Profile inMatchProfile = Profile.getByUuid(inMatchPlayer.getUniqueId());

                    if (!inMatchProfile.getOptions().isAllowSpectators() && !player.hasPermission("technique.staff")) {
                        player.sendMessage(CC.RED + "This match includes player that is not allowing spectators.");
                        return;
                    }
                }
            }
        }

        playerProfile.setSpectating(target);

        if (targetProfile.isInFight()) {
            targetProfile.getMatch().addSpectator(player, target);
        } else if (targetProfile.isInSumo()) {
            targetProfile.getSumo().addSpectator(player);
        } else if (targetProfile.isInBrackets()) {
            targetProfile.getBrackets().addSpectator(player);
        } else if (targetProfile.isInLMS()) {
            targetProfile.getLms().addSpectator(player);
        } else if (targetProfile.isInJuggernaut()) {
            targetProfile.getJuggernaut().addSpectator(player);
        } else if (targetProfile.isInParkour()) {
            targetProfile.getParkour().addSpectator(player);
        } else if (targetProfile.isInWipeout()) {
            targetProfile.getWipeout().addSpectator(player);
        } else if (targetProfile.isInSkyWars()) {
            targetProfile.getSkyWars().addSpectator(player);
        } else if (targetProfile.isInSpleef()) {
            targetProfile.getSpleef().addSpectator(player);
        } else {
            player.sendMessage(CC.RED + "That player is not in a match or running event.");
        }

    }

}
