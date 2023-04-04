package scha.efer.technique.scoreboard.adapter;

import gg.smok.core.plugin.SmokCore;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.selection.GoldenHeads;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.event.impl.brackets.Brackets;
import scha.efer.technique.event.impl.infected.Infected;
import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.event.impl.lms.LMS;
import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.event.impl.skywars.SkyWars;
import scha.efer.technique.event.impl.spleef.Spleef;
import scha.efer.technique.event.impl.sumo.Sumo;
import scha.efer.technique.event.impl.tournament.Tournament;
import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.MatchState;
import scha.efer.technique.match.kits.Bard;
import scha.efer.technique.match.kits.utils.ArmorClass;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.party.Party;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.queue.Queue;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.scoreboard.AssembleAdapter;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.TimeUtil;
import me.activated.core.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

    private final TechniquePlugin TechniquePlugin;

    public ScoreboardAdapter(TechniquePlugin TechniquePlugin) {
        this.TechniquePlugin = TechniquePlugin;
    }

    @Override
    public String getTitle(Player player) {
        return "&5&lStrafe &7⎜ &fTechnique";
    } //⎜

    @Override
    public List<String> getLines(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        PlayerData playerData = SmokCore.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (!profile.getOptions().isShowScoreboard()) {
            return null;
        }

        List<String> toReturn = new ArrayList<>();

        toReturn.add(CC.SB_BAR);

        if ((profile.isInLobby())) {
            if(profile.isSilent()) {
                toReturn.add("&4&lYou are in silent mode!");
            }
            toReturn.add("&fOnline: &5" + Bukkit.getServer().getOnlinePlayers().size());
            toReturn.add("&fFighting: &5" + getInFights());
            toReturn.add("&fQueueing: &5" + getInQueues());
            if (profile.isFollowMode()) {
                toReturn.add("&fFollowing: &5" + profile.getFollowing().getName());
            }
            if (!TechniquePlugin.getSumoManager().getCooldown().hasExpired()) {
                toReturn.add("&fSumo: &5" + TimeUtil.millisToTimer(TechniquePlugin.getSumoManager().getCooldown().getRemaining()));
            }
            if (!TechniquePlugin.getBracketsManager().getCooldown().hasExpired()) {
                toReturn.add("&fBrackets: &5" + TimeUtil.millisToTimer(TechniquePlugin.getBracketsManager().getCooldown().getRemaining()));
            }
            if (!TechniquePlugin.getLMSManager().getCooldown().hasExpired()) {
                toReturn.add("&fFFA: &5" + TimeUtil.millisToTimer(TechniquePlugin.getLMSManager().getCooldown().getRemaining()));
            }
            if (!TechniquePlugin.getParkourManager().getCooldown().hasExpired()) {
                toReturn.add("&fParkour: &5" + TimeUtil.millisToTimer(TechniquePlugin.getParkourManager().getCooldown().getRemaining()));
            }
            if (!TechniquePlugin.getWipeoutManager().getCooldown().hasExpired()) {
                toReturn.add("&fWipeout: &5" + TimeUtil.millisToTimer(TechniquePlugin.getWipeoutManager().getCooldown().getRemaining()));
            }
            if (!TechniquePlugin.getSkyWarsManager().getCooldown().hasExpired()) {
                toReturn.add("&fSkyWars: &5" + TimeUtil.millisToTimer(TechniquePlugin.getSkyWarsManager().getCooldown().getRemaining()));
            }
            if (!TechniquePlugin.getSpleefManager().getCooldown().hasExpired()) {
                toReturn.add("&fSpleef: &5" + TimeUtil.millisToTimer(TechniquePlugin.getSpleefManager().getCooldown().getRemaining()));
            }

            Clan clan = Clan.getByMember(player.getUniqueId());
            /*if (clan != null) {
                toReturn.add(" ");
                toReturn.add("&fClan: &5" + clan.getName());
                toReturn.add("&fClan ELO: &5" + clan.getElo());
                //toReturn.add("&fClan W/L: &5" + clan.getWins() + "/" + clan.getLoses());
            }*/
            toReturn.add(" ");
            toReturn.add("&fGlobal ELO: &5" + profile.getGlobalElo());
            toReturn.add("&fLeague: &5" + profile.getEloLeague());
            toReturn.add("&fCoins: &5" + playerData.getCoins());


            if (profile.getParty() != null) {
                Party party = profile.getParty();
                toReturn.add(" ");
                toReturn.add("&fParty: &5(" + party.getPlayers().size() + " Players)");
                toReturn.add("&fLeader: &5" + party.getLeader().getUsername());

            }

            if (Tournament.CURRENT_TOURNAMENT != null) {
                Tournament tournament = Tournament.CURRENT_TOURNAMENT;
                String round = tournament.getRound() > 0 ? Integer.toString(tournament.getRound()) : "&fStarting";
                toReturn.add("");
                toReturn.add("&5Tournament &f(" + tournament.getTeamCount() + "v" + tournament.getTeamCount() + ")");
                toReturn.add(" &f Kit: &5" + tournament.getLadder().getName());
                toReturn.add(" &f Round: &5" + round);
                toReturn.add((tournament.getTeamCount() > 1 ? " &fParties: &5" : "&fPlayers: &5") + tournament.getParticipatingCount());
            }
        } else if (profile.isInQueue()) {
            Queue queue = profile.getQueue();
            toReturn.clear();
            toReturn.add(CC.SB_BAR);
            toReturn.add("&fQueued for:");
            toReturn.add("&5 " + queue.getQueueName());
            toReturn.add("&f Queue Time: &5" + TimeUtil.millisToTimer(profile.getQueueProfile().getPassed()));
            toReturn.add("");
            toReturn.add("&f Global Queued: &5" + getInQueues());
            toReturn.add("&f " + queue.getKit().getName() +  " Queued: &5" + queue.getPlayers().size());
            if (queue.getQueueType().equals(QueueType.RANKED)) {
                toReturn.add(" &fElo Range: &5" + profile.getQueueProfile().getMinRange() + " &f- &5" + profile.getQueueProfile().getMaxRange());
            }
        } else if (profile.isInFFA()) {
            toReturn.add("&fIn FFA: &5" + getInFFAs());
            toReturn.add("&fLeague: &5" + profile.getEloLeague());
            toReturn.add("&fCoins: &5" + playerData.getCoins());
        } else if (profile.isInFight()) {
            Match match = profile.getMatch();

            if (match != null) {
                if (match.isSoloMatch()) {
                    TeamPlayer self = match.getTeamPlayer(player);
                    TeamPlayer opponent = match.getOpponentTeamPlayer(player);
                    Profile opponentProfile=Profile.getByUuid(opponent.getUuid());

                    if (match.getKit().getName().equalsIgnoreCase("Boxing")) {
                        toReturn.add("&fFighting: &5" + opponent.getUsername());
                        toReturn.add("&fDuration: &5" + match.getDuration());
                        toReturn.add("");
                        toReturn.add("&fHits: " + getHitStatus(match, player, opponent));
                        toReturn.add("&a You: &f" + self.getHits() + getComboSelfStatus(match, player, opponent));
                        toReturn.add("&c Them: &f" + opponent.getHits() + getComboOpponentStatus(match, player, opponent));
                        toReturn.add("");
                        toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                        toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                    } else if (match.getKit().getName().equalsIgnoreCase("Stick-Fight")) {
                        toReturn.add("&a[YOU] &7" + getSumoPoints(player));
                        toReturn.add("&c[THEM] &7" + getSumoPoints(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fCombos: &5" + self.getCombo());
                        toReturn.add("&fCPS: &a" + GoldenHeads.getCPS(player) + " &7┃ &c" + GoldenHeads.getCPS(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                        toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                    } else if (match.getKit().getName().equalsIgnoreCase("SkyWars")) {
                        toReturn.add("&fFighting: &5" + opponent.getUsername());
                        toReturn.add("&fDuration: &5" + match.getDuration());
                        toReturn.add("");
                        if (match.getState() == MatchState.FIGHTING) {
                            toReturn.add("&fPvP: &aEnabled");
                        } else {
                            toReturn.add("&fPvP: &cDisabled");
                        }
                        toReturn.add("");
                        toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                        toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                    } else if (match.getKit().getName().equalsIgnoreCase("Gladiator")) {
                        toReturn.add("&fFighting: &5" + opponent.getUsername());
                        toReturn.add("&fDuration: &5" + match.getDuration());
                        toReturn.add("");
                        if (match.getState() == MatchState.FIGHTING) {
                            toReturn.add("&fPvP: &aEnabled");
                        } else {
                            toReturn.add("&fPvP: &cDisabled");
                        }
                        toReturn.add("&fMode: &5Gladiator");
                        toReturn.add("");
                        toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                        toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                    } else if (match.getKit().getName().equalsIgnoreCase("Ladder-Clutch")) {
                        toReturn.add("&a[YOU] &7" + getSumoPoints(player));
                        toReturn.add("&c[THEM] &7" + getSumoPoints(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fCPS: &a" + GoldenHeads.getCPS(player) + " &7┃ &c" + GoldenHeads.getCPS(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                        toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                    } else if (match.getKit().getName().equalsIgnoreCase("MLG-Rush")) {
                        toReturn.add("&a[YOU] &7" + getSumoPoints(player));
                        toReturn.add("&c[THEM] &7" + getSumoPoints(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fCPS: &a" + GoldenHeads.getCPS(player) + " &7┃ &c" + GoldenHeads.getCPS(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                        toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                    }
                    else {
                        toReturn.add("&fFighting: &5" + opponent.getUsername());
                        toReturn.add("&fDuration: &5" + match.getDuration());
                        toReturn.add("");
                        toReturn.add("&fCPS: &a" + GoldenHeads.getCPS(player) + " &7┃ &c" + GoldenHeads.getCPS(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                        toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                    }

                }
                else if (match.getKit().getName().equalsIgnoreCase("Boxing")) {
                    TeamPlayer self=match.getTeamPlayer(player);
                    TeamPlayer opponent=match.getOpponentTeamPlayer(player);
                    toReturn.add("&fFighting: &5" + opponent.getUsername());
                    toReturn.add("&fDuration: &5" + match.getDuration());
                    toReturn.add("");
                    toReturn.add("&fHits: " + getHitStatus(match, player, opponent));
                    toReturn.add("&a You: &f" + self.getHits() + getComboSelfStatus(match, player, opponent));
                    toReturn.add("&c Them: &f" + opponent.getHits() + getComboOpponentStatus(match, player, opponent));
                    toReturn.add("");
                    toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                    toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                }
                else if (match.getKit().getName().equalsIgnoreCase("Sumo") || match.getKit().getName().equalsIgnoreCase("Block-Sumo") || match.getKit().getGameRules().isSumo() || match.isSumoMatch()) {
                    TeamPlayer self = match.getTeamPlayer(player);
                    TeamPlayer opponent = match.getOpponentTeamPlayer(player);

                    Profile targetProfile = Profile.getByUuid(opponent.getUuid());

                    int selfPoints = profile.getSumoRounds();
                    int opPoints = targetProfile.getSumoRounds();

                    toReturn.add("&fFighting: &5" + opponent.getUsername());
                    toReturn.add("&fDuration: &5" + match.getDuration());
                    toReturn.add("");
                    toReturn.add("&fPoints: &a" + selfPoints + " &7┃ &c" + opPoints + "");
                    toReturn.add("&fCPS: &a" + GoldenHeads.getCPS(player) + " &7┃ &c" + GoldenHeads.getCPS(opponent.getPlayer()));
                    toReturn.add("");
                    toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                    toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");

                } else if (match.getKit().getName().equalsIgnoreCase("Stick-Fight")) {
                    TeamPlayer self = match.getTeamPlayer(player);
                    TeamPlayer opponent = match.getOpponentTeamPlayer(player);

                    toReturn.add("&a[YOU] &7" + getSumoPoints(player));
                    toReturn.add("&c[THEM] &7" + getSumoPoints(opponent.getPlayer()));
                    toReturn.add("");
                    toReturn.add("&fNo Fall-Damage: &cDisabled");
                    toReturn.add("&fCPS: &a" + GoldenHeads.getCPS(player) + " &7┃ &c" + GoldenHeads.getCPS(opponent.getPlayer()));
                    toReturn.add("");
                    toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                    toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                } else if (match.getKit().getName().equalsIgnoreCase("MLG-Rush")) {
                    TeamPlayer self = match.getTeamPlayer(player);
                    TeamPlayer opponent = match.getOpponentTeamPlayer(player);
                    Profile opponentProfile=Profile.getByUuid(opponent.getUuid());

                    toReturn.add("&fRounds:");
                    toReturn.add("&f You: &5" + profile.getSumoRounds());
                    toReturn.add("&f Them: &5" + opponentProfile.getSumoRounds());
                    toReturn.add("");
                    toReturn.add("&fCPS: &a" + GoldenHeads.getCPS(player) + " &7┃ &c" + GoldenHeads.getCPS(opponent.getPlayer()));
                    toReturn.add("");
                    toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                    toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");
                }

                else if (match.isTheBridgeMatch()) {
                        TeamPlayer self=match.getTeamPlayer(player);
                        TeamPlayer opponent=match.getOpponentTeamPlayer(player);

                        Profile targetProfile=Profile.getByUuid(opponent.getUuid());

                        int selfPoints=profile.getBridgeRounds();
                        int opPoints=targetProfile.getBridgeRounds();

                        toReturn.add("&a[YOU] &7" + getBridgeRoundsLayout(player));
                        toReturn.add("&c[THEM] &7" + getBridgeRoundsLayout(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fCPS: &a" + GoldenHeads.getCPS(player) + " &7┃ &c" + GoldenHeads.getCPS(opponent.getPlayer()));
                        toReturn.add("");
                        toReturn.add("&fYour Ping: &5" + self.getPing() + "ms");
                        toReturn.add("&fEnemy Ping: &5" + opponent.getPing() + "ms");

                } //match end scoreboard :todo
                else if (match.isSumoTeamMatch()) {
                    Team team = match.getTeam(player);
                    Team opponentTeam = match.getOpponentTeam(player);

                    if ((team.getPlayers().size() + opponentTeam.getPlayers().size()) == 2) {
                        Team self = match.getTeam(player);
                        Team opponent = match.getOpponentTeam(self);

                        int selfPoints = self.getSumoRounds();
                        int opPoints = opponent.getSumoRounds();

                        toReturn.add("&fOpponent: &5" + opponent.getTeamPlayers().get(0).getUsername());
                        toReturn.add("&fPing: &5" + self.getTeamPlayers().get(0).getPing() + "ms &7┃ &5" + opponent.getTeamPlayers().get(0).getPing() + "ms");
                        toReturn.add("&fPoints: &5" + selfPoints + " &7┃ &5" + opPoints + "");

                    } else {
                        int selfPoints = team.getSumoRounds();
                        int opPoints = opponentTeam.getSumoRounds();

                        toReturn.add("&fYour Team: &a" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                        toReturn.add("&fEnemy Team: &5" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size());
                        toReturn.add("&fPoints: &5" + selfPoints + " &7┃ &5" + opPoints + "");
                    }
                } else if (match.isTeamMatch() || match.isHCFMatch()) {
                    Team team = match.getTeam(player);
                    Team opponentTeam = match.getOpponentTeam(player);

                    if ((team.getPlayers().size() + opponentTeam.getPlayers().size()) == 2) {

                        toReturn.add("&fOpponent: &5" + opponentTeam.getTeamPlayers().get(0).getUsername());
                        toReturn.add("&fPing: &5" + team.getTeamPlayers().get(0).getPing() + "ms &7┃ &5" + opponentTeam.getTeamPlayers().get(0).getPing() + "ms");
                    }
                    else if (team.getPlayers().size() == 2 && opponentTeam.getPlayers().size() == 2) {
                        TeamPlayer teammate = null;
                        for (TeamPlayer teamPlayer : team.getTeamPlayers()) {
                            if (teamPlayer != match.getTeamPlayer(player)) {
                                teammate = teamPlayer;
                            }
                        }

                        assert teammate != null;

                        if(teammate.isAlive()) {
                            toReturn.add("&a" + teammate.getUsername());
                            toReturn.add(getHearts(teammate.getPlayer()) + " &f" + getDivider() + " " + getPots(teammate.getPlayer()));
                        } else {
                            toReturn.add("&f&m" + teammate.getUsername());
                            toReturn.add("&5RIP");
                        }
                        toReturn.add("");
                        toReturn.add("&5&lOpponents");
                        for (TeamPlayer op : opponentTeam.getTeamPlayers()) {
                            if (op.isAlive()) {
                                toReturn.add(" &f" + op.getUsername());
                            } else {
                                toReturn.add(" &f&m" + op.getUsername());
                            }
                        }

                    } else {
                        toReturn.add("&fYour Team: &a" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                        toReturn.add("&fEnemy Team: &5" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size());
                    }

                    if (match.isHCFMatch()) {
                        ArmorClass pvpClass = TechniquePlugin.getArmorClassManager().getEquippedClass(player);
                        if (pvpClass != null) {
                            if (pvpClass instanceof Bard) {
                                Bard bardClass = (Bard) pvpClass;
                                toReturn.add("&eBard Energy: &r" + bardClass.getEnergy(player));
                            }
                        }
                    }
                } else if (match.isKoTHMatch()) {
                    Team team = match.getTeam(player);
                    Team opponentTeam = match.getOpponentTeam(player);
                    toReturn.add("&fYour Points: &a" + team.getKothPoints() + "/5");
                    toReturn.add("&fEnemy Points: &5" + opponentTeam.getKothPoints() + "/5");
                    toReturn.add("");
                    toReturn.add("&fDuration: &5" + match.getDuration());
                    toReturn.add("");
                    toReturn.add("&fCapper: &5" + (match.getCapper() != null ? match.getCapper().getName() : "Nobody"));
                    toReturn.add("&fCapture Time: &5" + match.getTimer() + "s");
                } else if (match.isFreeForAllMatch()) {
                    Team team = match.getTeam(player);
                    toReturn.add("&fOpponents: &5" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                    toReturn.add("&fDuration: &5" + match.getDuration());
                }
            }
        } else if (profile.isSpectating()) {
            Match match = profile.getMatch();
            Sumo sumo = profile.getSumo();
            LMS lms = profile.getLms();
            Juggernaut juggernaut = profile.getJuggernaut();
            Brackets brackets = profile.getBrackets();
            Parkour parkour = profile.getParkour();
            Wipeout wipeout = profile.getWipeout();
            SkyWars skyWars = profile.getSkyWars();
            Spleef spleef = profile.getSpleef();
            Infected infected = profile.getInfected();

            if (match != null) {
                if (!match.isHCFMatch() && !match.isKoTHMatch()) {
                    toReturn.add("&fKit: &5" + match.getKit().getName());
                }

                if (match.isSoloMatch() || match.getKit().getName().equalsIgnoreCase("Boxing")|| match.getKit().getName().equalsIgnoreCase("Stick-Fight")) {
                    toReturn.add("&fDuration: &5" + match.getDuration());
                    toReturn.add("&f");
                    toReturn.add("&5" + profile.getSpectating().getName() + ": &f" + PlayerUtil.getPing(profile.getSpectating()) + "ms");
                    toReturn.add("&d" + match.getOpponentPlayer(profile.getSpectating()).getName() + ": &f" + PlayerUtil.getPing(match.getOpponentPlayer(profile.getSpectating())) + "ms");
                }
                else if (match.getKit().getName().equalsIgnoreCase("Sumo")) {
                    TeamPlayer self = match.getTeamPlayer(profile.getSpectating());
                    TeamPlayer opponent = match.getOpponentTeamPlayer(player);

                    Profile epicProfile = Profile.getByUuid(self.getUuid());
                    Profile targetProfile = Profile.getByUuid(opponent.getUuid());

                    int selfPoints = epicProfile.getSumoRounds();
                    int opPoints = targetProfile.getSumoRounds();

                    toReturn.add("&fDuration: &5" + match.getDuration());
                    toReturn.add("&f");
                    toReturn.add("&5" + profile.getSpectating().getName() + ": &f" + PlayerUtil.getPing(profile.getSpectating()) + "ms");
                    toReturn.add("&d" + match.getOpponentPlayer(profile.getSpectating()).getName() + ": &f" + PlayerUtil.getPing(match.getOpponentPlayer(profile.getSpectating())) + "ms");
                    toReturn.add("&fPoints: &5" + selfPoints + " &7┃ &d" + opPoints + "");
                } else if (match.isTeamMatch() || match.isHCFMatch() || match.isKoTHMatch() || match.isSumoTeamMatch()) {
                    toReturn.add("");
                    toReturn.add("&a" + match.getTeamA().getLeader().getUsername() + "'s Team");
                    toReturn.add("&fvs");
                    toReturn.add("&5" + match.getTeamB().getLeader().getUsername() + "'s Team");
                } else {
                    toReturn.add("");
                    Team team = match.getTeam(player);
                    toReturn.add("&fAlive: &5" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                }
            } else if (sumo != null) {
                toReturn.add(CC.translate("&fHost: &5" + sumo.getName()));

                if (sumo.isWaiting()) {
                    toReturn.add("&f* &fPlayers: &5" + sumo.getEventPlayers().size() + "/" + sumo.getMaxPlayers());
                    toReturn.add("");

                    if (sumo.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fRemaining: &5" + sumo.getRemainingPlayers().size() + "/" + sumo.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + sumo.getRoundDuration());
                    toReturn.add("");
                    toReturn.add("&a" + sumo.getRoundPlayerA().getUsername());
                    toReturn.add("&fvs");
                    toReturn.add("&c" + sumo.getRoundPlayerB().getUsername());
                }
            } else if (lms != null) {
                toReturn.add(CC.translate("&fHost: &5" + lms.getName()));

                if (lms.isWaiting()) {
                    toReturn.add("&f&fPlayers: &5" + lms.getEventPlayers().size() + "/" + lms.getMaxPlayers());
                    toReturn.add("");

                    if (lms.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(lms.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fRemaining: &5" + lms.getRemainingPlayers().size() + "/" + lms.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + lms.getRoundDuration());
                }
            } else if (juggernaut != null) {
                toReturn.add(CC.translate("&fHost: &5" + juggernaut.getName()));
                toReturn.add(CC.translate("&fJuggernaut: &5" + juggernaut.getJuggernaut().getPlayer().getName()));

                if (juggernaut.isWaiting()) {
                    toReturn.add("&f* &fPlayers: &5" + juggernaut.getEventPlayers().size() + "/" + juggernaut.getMaxPlayers());
                    toReturn.add("");

                    if (juggernaut.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(juggernaut.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fRemaining: &5" + juggernaut.getRemainingPlayers().size() + "/" + juggernaut.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + juggernaut.getRoundDuration());
                }
            } else if (infected != null) {
                toReturn.add(CC.translate("&fHost: &5" + infected.getName()));

                if (infected.isWaiting()) {
                    toReturn.add("&f* &fPlayers: &5" + infected.getEventPlayers().size() + "/" + infected.getMaxPlayers());
                    toReturn.add("");

                    if (infected.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(infected.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fSurvivors: &a" + infected.getSurvivorPlayers().size() + "/" + infected.getTotalPlayers());
                    toReturn.add("&fInfected: &5" + infected.getInfectedPlayers().size() + "/" + infected.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + infected.getRoundDuration());
                }
            } else if (brackets != null) {
                toReturn.add(CC.translate("&fHost: &5" + brackets.getName()));

                if (brackets.isWaiting()) {
                    toReturn.add("&f* &fPlayers: &5" + brackets.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
                    toReturn.add("");

                    if (brackets.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(brackets.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fRemaining: &5" + brackets.getRemainingPlayers().size() + "/" + brackets.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + brackets.getRoundDuration());
                    toReturn.add("");
                    toReturn.add("&a" + brackets.getRoundPlayerA().getUsername());
                    toReturn.add("vs");
                    toReturn.add("&5" + brackets.getRoundPlayerB().getUsername());
                }
            } else if (parkour != null) {
                toReturn.add(CC.translate("&fHost: &5" + parkour.getName()));

                if (parkour.isWaiting()) {
                    toReturn.add("&f* &fPlayers: &5" + parkour.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
                    toReturn.add("");

                    if (parkour.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(parkour.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fRemaining: &5" + parkour.getRemainingPlayers().size() + "/" + parkour.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + parkour.getRoundDuration());
                }
            } else if (wipeout != null) {
                toReturn.add(CC.translate("&fHost: &5" + wipeout.getName()));

                if (wipeout.isWaiting()) {
                    toReturn.add("&f* &fPlayers: &5" + wipeout.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
                    toReturn.add("");

                    if (wipeout.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(wipeout.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fRemaining: &5" + wipeout.getRemainingPlayers().size() + "/" + wipeout.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + wipeout.getRoundDuration());
                }
            } else if (skyWars != null) {
                toReturn.add(CC.translate("&fHost: &5" + skyWars.getName()));

                if (skyWars.isWaiting()) {
                    toReturn.add("&f* &fPlayers: &r" + skyWars.getEventPlayers().size() + "/" + skyWars.getMaxPlayers());
                    toReturn.add("");

                    if (skyWars.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(skyWars.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fRemaining: &5" + skyWars.getRemainingPlayers().size() + "/" + skyWars.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + skyWars.getRoundDuration());
                }
            } else if (spleef != null) {
                toReturn.add(CC.translate("&fHost: &5" + spleef.getName()));

                if (spleef.isWaiting()) {
                    toReturn.add("&f* &fPlayers: &5" + spleef.getEventPlayers().size() + "/" + spleef.getMaxPlayers());
                    toReturn.add("");

                    if (spleef.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining = TimeUtil.millisToSeconds(spleef.getCooldown().getRemaining());

                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }

                        toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                    }
                } else {
                    toReturn.add("&fRemaining: &5" + spleef.getRemainingPlayers().size() + "/" + spleef.getTotalPlayers());
                    toReturn.add("&fDuration: &5" + spleef.getRoundDuration());
                }
            }
        } else if (profile.isInSumo()) {
            Sumo sumo = profile.getSumo();

            toReturn.add(CC.translate("&fHost: &5" + sumo.getName()));

            if (sumo.isWaiting()) {
                toReturn.add("&fPlayers: &5" + sumo.getEventPlayers().size() + "/" + sumo.getMaxPlayers());
                toReturn.add("");

                if (sumo.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add("&fRemaining: &5" + sumo.getRemainingPlayers().size() + "/" + sumo.getTotalPlayers());
                toReturn.add("&fDuration: &5" + sumo.getRoundDuration());
                toReturn.add("");
                toReturn.add("&a" + sumo.getRoundPlayerA().getUsername());
                toReturn.add("vs");
                toReturn.add("&c" + sumo.getRoundPlayerB().getUsername());
            }

        } else if (profile.isInBrackets()) {

            Brackets brackets = profile.getBrackets();

            toReturn.add(CC.translate("&fHost: &5" + brackets.getName()));

            if (brackets.isWaiting()) {
                toReturn.add("&f* &fPlayers: &5" + brackets.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
                toReturn.add("");

                if (brackets.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(brackets.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0.0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add("&fRemaining: &5" + brackets.getRemainingPlayers().size() + "/" + brackets.getTotalPlayers());
                toReturn.add("&fDuration: &5" + brackets.getRoundDuration());
                toReturn.add("");
                toReturn.add("&a" + brackets.getRoundPlayerA().getUsername());
                toReturn.add("vs");
                toReturn.add("&c" + brackets.getRoundPlayerB().getUsername());
            }
        } else if (profile.isInLMS()) {

            LMS lms = profile.getLms();

            toReturn.add(CC.translate("&fHost: &5" + lms.getName()));

            if (lms.isWaiting()) {
                toReturn.add("&fPlayers: &5" + lms.getEventPlayers().size() + "/" + lms.getMaxPlayers());
                toReturn.add("");

                if (lms.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(lms.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0.0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add("&fRemaining: &5" + lms.getRemainingPlayers().size() + "/" + lms.getTotalPlayers());
                toReturn.add("&fDuration: &5" + lms.getRoundDuration());
            }
        } else if (profile.isInJuggernaut()) {

            Juggernaut juggernaut = profile.getJuggernaut();

            toReturn.add(CC.translate("&fHost: &5" + juggernaut.getName()));

            if (juggernaut.isWaiting()) {
                toReturn.add("&f* &fPlayers: &5" + juggernaut.getEventPlayers().size() + "/" + juggernaut.getMaxPlayers());
                toReturn.add("");

                if (juggernaut.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(juggernaut.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0.0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add(CC.translate("&fJuggernaut: &5" + juggernaut.getJuggernaut().getPlayer().getName()));
                toReturn.add("&fRemaining: &5" + juggernaut.getRemainingPlayers().size() + "/" + juggernaut.getTotalPlayers());
                toReturn.add("&fDuration: &5" + juggernaut.getRoundDuration());
            }
        } else if (profile.isInInfected()) {

            Infected infected = profile.getInfected();

            toReturn.add(CC.translate("&fHost: &5" + infected.getName()));

            if (infected.isWaiting()) {
                toReturn.add("&f* &fPlayers: &5" + infected.getEventPlayers().size() + "/" + infected.getMaxPlayers());
                toReturn.add("");

                if (infected.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(infected.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0.0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add("&aSurvivors: &r" + infected.getSurvivorPlayers().size() + "/" + infected.getTotalPlayers());
                toReturn.add("&5Infected: &r" + infected.getInfectedPlayers().size() + "/" + infected.getTotalPlayers());
                toReturn.add("&fDuration: &5" + infected.getRoundDuration());
            }
        } else if (profile.isInParkour()) {

            Parkour parkour = profile.getParkour();

            toReturn.add(CC.translate("&fHost: &5" + parkour.getName()));

            if (parkour.isWaiting()) {
                toReturn.add("&f* &fPlayers: &5" + parkour.getEventPlayers().size() + "/" + parkour.getMaxPlayers());
                toReturn.add("");

                if (parkour.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(parkour.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0.0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add("&fRemaining: &5" + parkour.getRemainingPlayers().size() + "/" + parkour.getTotalPlayers());
                toReturn.add("&fDuration: &5" + parkour.getRoundDuration());
            }
        } else if (profile.isInWipeout()) {

            Wipeout wipeout = profile.getWipeout();

            toReturn.add(CC.translate("&fHost: &5" + wipeout.getName()));

            if (wipeout.isWaiting()) {
                toReturn.add("&f* &fPlayers: &5" + wipeout.getEventPlayers().size() + "/" + wipeout.getMaxPlayers());
                toReturn.add("");

                if (wipeout.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(wipeout.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0.0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add("&fRemaining: &5" + wipeout.getRemainingPlayers().size() + "/" + wipeout.getTotalPlayers());
                toReturn.add("&fDuration: &5" + wipeout.getRoundDuration());
            }
        } else if (profile.isInSkyWars()) {

            SkyWars skyWars = profile.getSkyWars();

            toReturn.add(CC.translate("&fHost: &5" + skyWars.getName()));

            if (skyWars.isWaiting()) {
                toReturn.add("&f* &fPlayers: &5" + skyWars.getEventPlayers().size() + "/" + skyWars.getMaxPlayers());
                toReturn.add("");

                if (skyWars.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(skyWars.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0.0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add("&fRemaining: &5" + skyWars.getRemainingPlayers().size() + "/" + skyWars.getTotalPlayers());
                toReturn.add("&fDuration: &5" + skyWars.getRoundDuration());
            }
        } else if (profile.isInSpleef()) {

            Spleef spleef = profile.getSpleef();

            toReturn.add(CC.translate("&fHost: &5" + spleef.getName()));

            if (spleef.isWaiting()) {
                toReturn.add("&f* &fPlayers: &5" + spleef.getEventPlayers().size() + "/" + spleef.getMaxPlayers());
                toReturn.add("");

                if (spleef.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining = TimeUtil.millisToSeconds(spleef.getCooldown().getRemaining());

                    if (remaining.startsWith("-")) {
                        remaining = "0.0";
                    }

                    toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
                }
            } else {
                toReturn.add("&fRemaining: &5" + spleef.getRemainingPlayers().size() + "/" + spleef.getTotalPlayers());
                toReturn.add("&fDuration: &5" + spleef.getRoundDuration());
            }
        }

        toReturn.add("");
        toReturn.add("&5strafe.world");
        toReturn.add(CC.SB_BAR);

        return toReturn;
    }


    public int getInQueues() {
        int inQueues = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                if (profile.isInQueue()) {
                    inQueues++;
                }
            }
        }

        return inQueues;
    }

    public int getInFFAs() {
        int inFFAs = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                if (profile.isInFFA()) {
                    inFFAs++;
                }
            }
        }

        return inFFAs;
    }

    public int getInFights() {
        int inFights = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null) {
                if (profile.isInFight() || profile.isInEvent()) {
                    inFights++;
                }
            }
        }

        return inFights;
    }

    public String getHearts(Player player) {
        double health = player.getHealth() / 2;
        String color = "&a";

        if(health <= 8.5) {
            color = "&e";
        } else if (health <= 5.5) {
            color = "&6";
        } else if (health <= 4.5) {
            color = "&5";
        }

        DecimalFormat twoDForm = new DecimalFormat("#.#");

        return CC.translate(color + twoDForm.format(health) + " ❤");
    }

    public String getPots(Player player) {
        String color = "&a";
        int pots = Profile.getByUuid(player.getUniqueId()).getMatch().getTeamPlayer(player).getPotions();

        if (pots <= 3) {
            color = "&5";
        } else if (pots <= 8) {
            color = "&5";
        } else if (pots <= 12) {
            color = "&6";
        } else if (pots <= 20) {
            color = "&e";
        }

        return CC.translate(color + pots + " pots");
    }

    public String getHitStatus(Match match, Player player, TeamPlayer opponent) {
        TeamPlayer profile = Profile.getByUuid(player.getUniqueId()).getMatch().getTeamPlayer(player);
        TeamPlayer opProfile = Profile.getByUuid(opponent.getPlayer().getUniqueId()).getMatch().getTeamPlayer(opponent.getPlayer());
        if (profile.getHits() > opProfile.getHits()) {
            return CC.GREEN + "(+" + (profile.getHits() - opProfile.getHits()) + ")";
        } else if (profile.getHits() < opProfile.getHits()) {
            return CC.RED + "(-" + (opProfile.getHits() - profile.getHits()) + ")";
        } else if (profile.getHits() == opProfile.getHits()) {
            return CC.GREEN + "(0)";
        }
        return CC.DARK_RED + "(?)";
    }

    public String getComboSelfStatus(Match match, Player player, TeamPlayer opponent) {
        TeamPlayer profile = Profile.getByUuid(player.getUniqueId()).getMatch().getTeamPlayer(player);
        if (profile.getCombo() > 1) {
            return CC.YELLOW + " (" + profile.getCombo() + " Combo)";
        } else {
            return " ";
        }
    }

    public String getComboOpponentStatus(Match match, Player player, TeamPlayer opponent) {
        TeamPlayer profile = Profile.getByUuid(player.getUniqueId()).getMatch().getTeamPlayer(player);
        TeamPlayer opProfile = Profile.getByUuid(opponent.getPlayer().getUniqueId()).getMatch().getTeamPlayer(opponent.getPlayer());
        if (opProfile.getCombo() > 1) {
            return CC.YELLOW + " (" + opProfile.getCombo() + " Combo)";
        } else {
            return " ";
        }
    }

    public String getSumoPoints(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getSumoRounds() == 0) {
            return CC.GRAY + "⬤⬤⬤";
        } else if (profile.getSumoRounds() == 1) {
            return CC.GREEN + "⬤" + CC.GRAY + "⬤⬤";
        } else if (profile.getSumoRounds() == 2) {
            return CC.GREEN + "⬤⬤" + CC.GRAY + "⬤";
        } else if (profile.getSumoRounds() == 3) {
            return CC.GREEN + "⬤⬤⬤";
        }
        return "⬤⬤⬤";
    }

    public String getBridgeRoundsLayout(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getBridgeRounds() == 0) {
            return CC.GRAY + "███";
        } else if (profile.getBridgeRounds() == 1) {
            return CC.GREEN + "█" + CC.GRAY + "██";
        } else if (profile.getBridgeRounds() == 2) {
            return CC.GREEN + "██" + CC.GRAY + "█";
        } else if (profile.getBridgeRounds() == 3) {
            return CC.GREEN + "███";
        }
        return "███";
    }

    public String getDivider() {
        return "┃";
    }

}
