package scha.efer.technique.event.impl.tournament;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.impl.SumoTeamMatch;
import scha.efer.technique.match.impl.TeamMatch;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.party.Party;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.util.external.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Tournament {
    public static BukkitRunnable RUNNABLE = null;
    public static Tournament CURRENT_TOURNAMENT = null;

    @Getter
    private final List<Party> participants = new ArrayList<Party>() {
        @Override
        public Iterator<Party> iterator() {
            filter();
            return super.iterator();
        }

        @Override
        public int size() {
            filter();
            return super.size();
        }

        private void filter() {

            List<Party> toRemove = Lists.newArrayList();
            for (int i = 0; i < super.size(); i++) {
                Party party = get(i);
                if (party.isDisbanded()) {
                    toRemove.add(party);
                }
            }
            removeAll(toRemove);
        }
    };

    private final List<TournamentMatch> tournamentMatches = Lists.newArrayList();
    private final List<SumoTournamentMatch> sumoTournamentMatches = Lists.newArrayList();

    @Getter
    @Setter
    private Kit ladder;
    @Getter
    @Setter
    private String hostType;
    private int participatingCount;
    private int round;
    @Getter
    @Setter
    private int teamCount;
    private boolean canceled = false;


    public boolean isParticipating(Player player) {
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            return false;
        }
        return participants.contains(Profile.getByUuid(player.getUniqueId()).getParty());
    }

    public boolean hasStarted() {
        return round != 0;
    }

/*    public int getStartingParticipatingCount() {
        if (participatingCount == 0) {
            return getParticipatingCount();
        }
        return participatingCount;
    }*/

    public int getParticipatingCount() {
        return participants.size();
    }

    public List<TournamentMatch> getTournamentMatches() {
        return tournamentMatches;
    }
    public List<SumoTournamentMatch> getSumoTournamentMatches() {
        return sumoTournamentMatches;
    }

    public int getRound() {
        return round;
    }

    public void leave(Party party) {
        Preconditions.checkState(round == 0, "Can not join after start.");
        if (participants.contains(party)) {
            for (Party partyparticipants : participants) {
                for (Player player : partyparticipants.getPlayers()) {
                    player.sendMessage(
                            CC.RED + CC.translate(TechniquePlugin.get().getSmokHook().getPlayerColor(party.getLeader().getPlayer()))
                                    + CC.GRAY + " has left the tournament. " + CC.RED + "(" + participants.size() + "/500)");
                }
            }
            participants.remove(party);
        }
    }

    public void participate(Party party) {
        Preconditions.checkState(round == 0, "Can not join after start.");
        if (!participants.contains(party)) {
            participants.add(party);
            for (Party partyparticipants : participants) {
                for (Player player : partyparticipants.getPlayers()) {
                    player.sendMessage(
                            CC.RED + CC.translate(TechniquePlugin.get().getSmokHook().getPlayerColor(party.getLeader().getPlayer()))
                                    + CC.GRAY + " has joined the tournament. " + CC.RED + "(" + participants.size() + "/500)");
                }
            }
        }
    }

    public void cancel() {
        canceled = true;
        participants.clear();
        tournamentMatches.clear();
        sumoTournamentMatches.clear();
    }

    public void tournamentstart() {
        Collections.shuffle(participants);
        if (participatingCount == 0) {
            participatingCount = participants.size();
        }
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Round #" + CC.RED + (round++ + 1) + ChatColor.WHITE + " has started.");

        Iterator<Party> iterator = participants.iterator();
        while (iterator.hasNext()) {
            Party player = iterator.next();
            if (!iterator.hasNext()) {
                player.broadcast(ChatColor.DARK_PURPLE + "You do not have an opponent this round. Skipping...");
                break;
            }
            Party other = iterator.next();
            Arena arena = Arena.getRandom(ladder);
            TournamentMatch tournamentMatch = null;
            SumoTournamentMatch sumoTournamentMatch = null;


            {
                Team teamA = new Team(new TeamPlayer(player.getLeader().getPlayer()));
                Team teamB = new Team(new TeamPlayer(other.getLeader().getPlayer()));
                if (ladder.getGameRules().isSumo()) {
                    sumoTournamentMatch = new SumoTournamentMatch(teamA, teamB, getLadder(), arena);
                } else {
                    tournamentMatch = new TournamentMatch(teamA, teamB, getLadder(), arena);
                }

                for (Player player1 : player.getPlayers()) {
                    final Profile otherData = Profile.getByUuid(player1.getUniqueId());

                    otherData.setState(ProfileState.IN_FIGHT);
                    if (ladder.getGameRules().isSumo()) {
                        otherData.setMatch(sumoTournamentMatch);
                    } else {
                        otherData.setMatch(tournamentMatch);
                    }

                    if (!player.isLeader(player1.getUniqueId())) {
                        teamA.getTeamPlayers().add(new TeamPlayer(player1));
                    }
                }
                for (Player player1 : other.getPlayers()) {
                    final Profile otherData = Profile.getByUuid(player1.getUniqueId());

                    otherData.setState(ProfileState.IN_FIGHT);
                    if (ladder.getGameRules().isSumo()) {
                        otherData.setMatch(sumoTournamentMatch);
                    } else {
                        otherData.setMatch(tournamentMatch);
                    }

                    if (!other.isLeader(player1.getUniqueId())) {
                        teamB.getTeamPlayers().add(new TeamPlayer(player1));
                    }
                }
            }
            if (ladder.getGameRules().isSumo() && sumoTournamentMatch != null) {
                sumoTournamentMatch.start();
                sumoTournamentMatches.add(sumoTournamentMatch);
            } else if (tournamentMatch != null) {
                tournamentMatch.start();
                tournamentMatches.add(tournamentMatch);
            }


        }
    }


    public class TournamentMatch extends TeamMatch {

        public TournamentMatch(Team teamA, Team teamB, Kit ladder, Arena arena) {
            super(teamA, teamB, ladder, arena);
        }

        @Override
        public void handleDeath(Player deadPlayer, Player killerPlayer, boolean disconnected) {
            super.handleDeath(deadPlayer, killerPlayer, disconnected);
        }

        @Override
        public boolean onEnd() {
            if (!canceled) {
                final Team winningTeam = this.getWinningTeam();
                final Team losingTeam = this.getOpponentTeam(winningTeam);
                for (TeamPlayer losingPlayer : losingTeam.getTeamPlayers()) {
                    participants.remove(Profile.getByUuid(losingPlayer.getUuid()).getParty());
                }
                tournamentMatches.remove(TournamentMatch.this);
                StringBuilder builder = new StringBuilder();

                for (TeamPlayer matchPlayer : winningTeam.getTeamPlayers()) {
                    builder.append(CC.translate(TechniquePlugin.get().getSmokHook().getPlayerColor(matchPlayer.getPlayer())));

                    builder.append("&f, ");
                }
                StringBuilder builders = new StringBuilder();
                for (TeamPlayer matchPlayer : losingTeam.getTeamPlayers()) {
                    builders.append(CC.translate(TechniquePlugin.get().getSmokHook().getPlayerColor(matchPlayer.getPlayer())));
                    builders.append("&f, ");
                }
                if (builders.length() > 0) {
                    builders.setLength(builders.length() - 2);
                }
                if (builder.length() > 0) {
                    builder.setLength(builder.length() - 2);
                }

                Bukkit.broadcastMessage(CC.translate(builders.toString()) + ChatColor.WHITE + " has been eliminated. " + "(" + participants.size() + "/" + participatingCount + ")");
                if (tournamentMatches.isEmpty()) {
                    if (participants.size() <= 1) {
                        Bukkit.broadcastMessage(CC.RED + CC.BOLD + "");
                        Bukkit.broadcastMessage(CC.translate(builder.toString()) + ChatColor.WHITE + " has won the tournament!");
                        Bukkit.broadcastMessage(CC.RED + CC.BOLD + "");
                        if (participants.get(0) != null) {
                            if (getTeamCount() == 1) {
                                if (getHostType().equalsIgnoreCase("console")) {
                                    Party winner = participants.get(0);
                                    Profile winnerProfile = Profile.getByUuid(winner.getLeader().getUuid());
                                    winnerProfile.getKitData().get(getKit()).setElo(winnerProfile.getKitData().get(getKit()).getElo() + 10);
                                }
                            }
                        }
                        CURRENT_TOURNAMENT = null;
                    } else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                tournamentstart();
                            }
                        }.runTaskLater(TechniquePlugin.get(), 100L);
                    }
                }
            }
            super.onEnd();
            return true;
        }
    }

    public class SumoTournamentMatch extends SumoTeamMatch {

        public SumoTournamentMatch(Team teamA, Team teamB, Kit ladder, Arena arena) {
            super(teamA, teamB, ladder, arena);
        }

        @Override
        public void handleDeath(Player deadPlayer, Player killerPlayer, boolean disconnected) {
            super.handleDeath(deadPlayer, killerPlayer, disconnected);
        }

        @Override
        public boolean onEnd() {
            if (!canceled) {
                final Team winningTeam = this.getWinningTeam();
                final Team losingTeam = this.getOpponentTeam(winningTeam);
                for (TeamPlayer losingPlayer : losingTeam.getTeamPlayers()) {
                    participants.remove(Profile.getByUuid(losingPlayer.getUuid()).getParty());
                }
                sumoTournamentMatches.remove(SumoTournamentMatch.this);
                StringBuilder builder = new StringBuilder();

                for (TeamPlayer matchPlayer : winningTeam.getTeamPlayers()) {
                    builder.append(CC.translate(TechniquePlugin.get().getSmokHook().getPlayerColor(matchPlayer.getPlayer())));

                    builder.append("&f, ");
                }
                StringBuilder builders = new StringBuilder();
                for (TeamPlayer matchPlayer : losingTeam.getTeamPlayers()) {
                    builders.append(CC.translate(TechniquePlugin.get().getSmokHook().getPlayerColor(matchPlayer.getPlayer())));
                    builders.append("&f, ");
                }
                if (builders.length() > 0) {
                    builders.setLength(builders.length() - 2);
                }
                if (builder.length() > 0) {
                    builder.setLength(builder.length() - 2);
                }

                Bukkit.broadcastMessage(CC.translate(builders.toString()) + ChatColor.WHITE + " has been eliminated. " + "(" + participants.size() + "/" + participatingCount + ")");
                if (sumoTournamentMatches.isEmpty()) {
                    if (participants.size() <= 1) {
                        Bukkit.broadcastMessage(CC.RED + CC.BOLD + "");
                        Bukkit.broadcastMessage(CC.translate(builder.toString()) + ChatColor.WHITE + " has won the tournament!");
                        Bukkit.broadcastMessage(CC.RED + CC.BOLD + "");
                        if (participants.get(0) != null) {
                            if (getTeamCount() == 1) {
                                if (getHostType().equalsIgnoreCase("console")) {
                                    Party winner = participants.get(0);
                                    Profile winnerProfile = Profile.getByUuid(winner.getLeader().getUuid());
                                    winnerProfile.getKitData().get(getKit()).setElo(winnerProfile.getKitData().get(getKit()).getElo() + 10);
                                }
                            }
                        }
                        CURRENT_TOURNAMENT = null;
                    } else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                tournamentstart();
                            }
                        }.runTaskLater(TechniquePlugin.get(), 100L);
                    }
                }
            }
            super.onEnd();
            return true;
        }
    }
}
