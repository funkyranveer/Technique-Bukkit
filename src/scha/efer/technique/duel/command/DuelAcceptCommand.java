package scha.efer.technique.duel.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.ArenaType;
import scha.efer.technique.arena.impl.StandaloneArena;
import scha.efer.technique.duel.DuelRequest;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.impl.*;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "duel accept")
public class DuelAcceptCommand {

    public void execute(Player player, @CPL("player") Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "That player is no longer online.");
            return;
        }

        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot duel a player while being frozen.");
            return;
        }

        if (target.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot duel a player who's frozen.");
            return;
        }

        Profile senderProfile = Profile.getByUuid(player.getUniqueId());

        if (senderProfile.isBusy(player)) {
            player.sendMessage(CC.RED + "You cannot duel anyone right now.");
            return;
        }

        Profile receiverProfile = Profile.getByUuid(target.getUniqueId());

        if (!receiverProfile.isPendingDuelRequest(player)) {
            player.sendMessage(CC.RED + "You do not have a pending duel request from " + target.getName() + CC.RED + ".");
            return;
        }

        if (receiverProfile.isBusy(target)) {
            player.sendMessage(CC.translate(CC.RED + target.getDisplayName()) + CC.RED + " is currently busy.");
            return;
        }

        DuelRequest request = receiverProfile.getSentDuelRequests().get(player.getUniqueId());

        if (request == null) {
            return;
        }

        if (request.isParty()) {
            if (senderProfile.getParty() == null) {
                player.sendMessage(CC.RED + "You do not have a party to duel with.");
                return;
            } else if (receiverProfile.getParty() == null) {
                player.sendMessage(CC.RED + "That player does not have a party to duel with.");
                return;
            }
        } else {
            if (senderProfile.getParty() != null) {
                player.sendMessage(CC.RED + "You cannot duel whilst in a party.");
                return;
            } else if (receiverProfile.getParty() != null) {
                player.sendMessage(CC.RED + "That player is in a party and cannot duel right now.");
                return;
            }
        }

        Arena arena = request.getArena();

        if (arena == null) {
            player.sendMessage(CC.RED + "Tried to start a match but the arena was invalid.");
            return;
        }

        if (arena.isActive()) {
            if (arena.getType().equals(ArenaType.STANDALONE)) {
                StandaloneArena sarena = (StandaloneArena) arena;
                if (sarena.getDuplicates() != null) {
                    boolean foundarena = false;
                    for (Arena darena : sarena.getDuplicates()) {
                        if (!darena.isActive()) {
                            arena = darena;
                            foundarena = true;
                            break;
                        }
                    }
                    if (!foundarena) {
                        player.sendMessage(CC.RED + "The arena you were dueled was a build match and there were no arenas found.");
                        return;
                    }
                }
            } else {
                player.sendMessage(CC.RED + "The arena you were dueled was a build match and there were no arenas found.");
                return;
            }
        }
        if (!arena.getType().equals(ArenaType.SHARED) && !arena.getType().equals(ArenaType.KOTH)) {
            arena.setActive(true);
        }

        Match match;

        if (request.isParty()) {
            if (request.getKit().getName().equals("HCFDIAMOND")) {
                Team teamA = new Team(new TeamPlayer(player));

                for (Player partyMember : senderProfile.getParty().getPlayers()) {
                    if (!partyMember.getPlayer().equals(player)) {
                        teamA.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }

                Team teamB = new Team(new TeamPlayer(target));

                for (Player partyMember : receiverProfile.getParty().getPlayers()) {
                    if (!partyMember.getPlayer().equals(target)) {
                        teamB.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }

                match = new HCFMatch(teamA, teamB, arena);
            } else {
                Team teamA = new Team(new TeamPlayer(player));

                for (Player partyMember : senderProfile.getParty().getPlayers()) {
                    if (!partyMember.getPlayer().equals(player)) {
                        teamA.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }

                Team teamB = new Team(new TeamPlayer(target));

                for (Player partyMember : receiverProfile.getParty().getPlayers()) {
                    if (!partyMember.getPlayer().equals(target)) {
                        teamB.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }

                if (request.getKit().getGameRules().isSumo()) {
                    match = new SumoTeamMatch(teamA, teamB, request.getKit(), arena);
                } else {
                    match = new TeamMatch(teamA, teamB, request.getKit(), arena);
                }

            }
        } else {
            if(request.getKit().getGameRules().isSumo()) {
                match = new SumoMatch(null, new TeamPlayer(player), new TeamPlayer(target), request.getKit(), arena, QueueType.UNRANKED);
            } else {
                match = new SoloMatch(null, new TeamPlayer(player), new TeamPlayer(target), request.getKit(), arena,
                        QueueType.UNRANKED, 0, 0);
            }
        }

        match.start();
    }


}
