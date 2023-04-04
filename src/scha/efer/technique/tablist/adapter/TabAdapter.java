package scha.efer.technique.tablist.adapter;

import scha.efer.technique.TechniquePlugin;
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
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.party.Party;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.tablist.ZigguratAdapter;
import scha.efer.technique.tablist.utils.BufferedTabObject;
import scha.efer.technique.util.external.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabAdapter implements ZigguratAdapter {


    @Override
    public String getHeader() {
        return "" + CC.translate(TechniquePlugin.get().getMainConfig().getString("TAB.HEADER"));
    }

    @Override
    public String getFooter() {
        return "" + CC.translate(TechniquePlugin.get().getMainConfig().getString("TAB.FOOTER"));
    }

    @Override
    public Set<BufferedTabObject> getSlots(Player player) {
        Set<BufferedTabObject> tabObjects = new HashSet<>();

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            return tabObjects;
        }


        /* --------------------- DEFAULT TAB SHIT ------------------- */

        List<Integer> takenSlots = new ArrayList<>();

        int[] ipSlots = new int[]{
                2, 22, 42, 20, 40, 60
        };

        tabObjects.add(new BufferedTabObject().slot(21).text(CC.translate("&5&lNA Practice")));
        tabObjects.add(new BufferedTabObject().slot(20).text(CC.translate("&fdiscord.strafe.world")));
        tabObjects.add(new BufferedTabObject().slot(40).text(CC.translate(TechniquePlugin.get().getMainConfig().getString("TAB.PROXY"))));
        tabObjects.add(new BufferedTabObject().slot(60).text(CC.translate("&fstore.strafe.world")));

        for (int ipSlot : ipSlots) {
            takenSlots.add(ipSlot);
        }

        takenSlots.add(0);
        takenSlots.add(1);
        takenSlots.add(3);
        takenSlots.add(21);
        takenSlots.add(23);
        takenSlots.add(41);
        takenSlots.add(43);

        tabObjects.add(new BufferedTabObject().slot(67).text("&4&lWARNING!"));

        tabObjects.add(new BufferedTabObject().slot(68).text("&7Please use"));

        tabObjects.add(new BufferedTabObject().slot(69).text("&71.7 for the"));

        tabObjects.add(new BufferedTabObject().slot(70).text("&7optimal playing"));

        tabObjects.add(new BufferedTabObject().slot(71).text("&7experience"));

        /* --------------------- TAB CONTENT SHIT ------------------- */

        if (profile.isInLobby() || profile.isInQueue()) {
            if (!profile.isInTournament(player)) {


                tabObjects.add(new BufferedTabObject().slot(3).text(CC.translate("&5&lInformation")));
                tabObjects.add(new BufferedTabObject().slot(4).text(CC.translate("&fOnline: &5" + Bukkit.getOnlinePlayers().size())));
                tabObjects.add(new BufferedTabObject().slot(5).text(CC.translate("&fQueueing: &5" + getInQueues())));
                tabObjects.add(new BufferedTabObject().slot(6).text(CC.translate("&fFighting: &5" + getInFights())));

                tabObjects.add(new BufferedTabObject().slot(23).text(CC.translate("&5&lStatistics")));
                int addedstats = 24;
                for (Kit kit : Kit.getKits()) {
                    if (kit.isEnabled()) {
                        if (kit.getGameRules().isRanked()) {
                            tabObjects.add(new BufferedTabObject().slot(addedstats).text(CC.DARK_PURPLE+ kit.getName() + ": " + CC.WHITE + profile.getKitData().get(kit).getElo()));
                            addedstats++;
                            if (addedstats > 39) {
                                break;
                            }
                        }
                    }
                }

                Clan clan = Clan.getByMember(player.getUniqueId());
                if (clan != null) {

                    tabObjects.add(new BufferedTabObject().slot(8).text(CC.translate("&5&lClan")));
                    tabObjects.add(new BufferedTabObject().slot(9).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.ELEMENTS") + "Name: " + TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + clan.getName()));
                    tabObjects.add(new BufferedTabObject().slot(10).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.ELEMENTS") + "Elo: " + TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + clan.getElo()));
                    tabObjects.add(new BufferedTabObject().slot(11).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.ELEMENTS") + "Online: " + TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + clan.getPlayerWhereOnline().size() + "/" + clan.getMembers().size()));

                }

                tabObjects.add(new BufferedTabObject().slot(43).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "Party List"));
                if (profile.getParty() != null) {
                    int addedparty = 44;
                    Party party = profile.getParty();
                    for (TeamPlayer teamPlayer : party.getTeamPlayers()) {
                        tabObjects.add(new BufferedTabObject().slot(addedparty).text(" &f" + (party.isLeader(teamPlayer.getUuid()) ? "* " : "- ") + TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + teamPlayer.getUsername()));
                        addedparty++;
                        if (addedparty >= 60) {
                            break;
                        }
                    }
                }
            } else {
                int added = 4;
                tabObjects.add(new BufferedTabObject().slot(23).text(CC.translate("&5&lTournament")));
                for (Tournament.TournamentMatch match : Tournament.CURRENT_TOURNAMENT.getTournamentMatches()) {
                    tabObjects.add(new BufferedTabObject().slot(added).text(CC.DARK_RED + match.getTeamA().getLeader().getPlayer().getName()));
                    tabObjects.add(new BufferedTabObject().slot(added + 20).text(CC.WHITE + "vs"));
                    tabObjects.add(new BufferedTabObject().slot(added + 40).text(CC.DARK_RED + match.getTeamB().getLeader().getPlayer().getName()));
                    added++;
                    if (added >= 20) {
                        break;
                    }
                }
            }
        } else if (profile.isInFight()) {
            Match match = profile.getMatch();
                                                    //TODO: Add colors to correspond with team name
            if (match != null) {
                if (match.isSoloMatch() || match.isSumoMatch() || match.isBoxingMatch() || match.isTheBridgeMatch() || match.isStickFightMatch()) {
                    TeamPlayer opponent = match.getOpponentTeamPlayer(player);

                    tabObjects.add(new BufferedTabObject().slot(4).text("&aYou"));
                    tabObjects.add(new BufferedTabObject().slot(23).text(CC.translate("&5&lMatch Info")));
                    tabObjects.add(new BufferedTabObject().slot(44).text("&5Enemy"));

                    tabObjects.add(new BufferedTabObject().slot(5).text("&a" + player.getName()));
                    tabObjects.add(new BufferedTabObject().slot(24).text(CC.WHITE + "Duration:"));
                    tabObjects.add(new BufferedTabObject().slot(25).text(CC.DARK_RED + match.getDuration()));
                    tabObjects.add(new BufferedTabObject().slot(45).text("&5" + opponent.getUsername()));
                    if (match.isSumoMatch()) {
                        Profile oppo = Profile.getByUuid(opponent.getUuid());
                        tabObjects.add(new BufferedTabObject().slot(6).text("&aPoints - " + profile.getSumoRounds()));
                        tabObjects.add(new BufferedTabObject().slot(46).text("&5Points - " + oppo.getSumoRounds()));
                    }
                } else if (match.isTeamMatch() || match.isHCFMatch() || match.isKoTHMatch() || match.isSumoTeamMatch()) {
                    Team team = match.getTeam(player);
                    Team opponentTeam = match.getOpponentTeam(player);
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 30) {
                        tabObjects.add(new BufferedTabObject().slot(4).text("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")"));

                        int added = 5;
                        for (TeamPlayer teamPlayer : team.getTeamPlayers()) {
                            tabObjects.add(new BufferedTabObject().slot(added).text(" " + (!teamPlayer.isAlive() || teamPlayer.isDisconnected() ? "&f&m" : "") + teamPlayer.getUsername()));
                            added++;
                            if (added >= 20) {
                                break;
                            }
                        }

                        tabObjects.add(new BufferedTabObject().slot(23).text(CC.DARK_RED + "&lMatch Info"));
                        tabObjects.add(new BufferedTabObject().slot(24).text("&fDuration:"));
                        tabObjects.add(new BufferedTabObject().slot(25).text(CC.DARK_RED + match.getDuration()));

                        int added2 = 45;
                        tabObjects.add(new BufferedTabObject().slot(44).text("&5Opponents &5(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")"));
                        for (TeamPlayer teamPlayer : opponentTeam.getTeamPlayers()) {
                            tabObjects.add(new BufferedTabObject().slot(added2).text(" " + (!teamPlayer.isAlive() || teamPlayer.isDisconnected() ? "&f&m" : "") + teamPlayer.getUsername()));
                            added2++;
                            if (added2 >= 60) {
                                break;
                            }
                        }

                        if (match.isSumoTeamMatch()) {
                            tabObjects.add(new BufferedTabObject().slot(27).text("&5&lPoints"));
                            tabObjects.add(new BufferedTabObject().slot(28).text("&aTeam &8- &f" + team.getSumoRounds()));
                            tabObjects.add(new BufferedTabObject().slot(28).text("&5Opponents &8- &f" + opponentTeam.getSumoRounds()));

                        }

                    }
                } else if (match.isFreeForAllMatch()) {
                    Team team = match.getTeam(player);
                    tabObjects.add(new BufferedTabObject().slot(23).text("&5&lMatch Info"));
                    tabObjects.add(new BufferedTabObject().slot(25).text("&fOpponents: &5" + team.getAliveCount() + "/" + team.getTeamPlayers().size()));
                    tabObjects.add(new BufferedTabObject().slot(27).text("&fDuration:"));
                    tabObjects.add(new BufferedTabObject().slot(28).text(CC.DARK_RED + match.getDuration()));
                }
            }
        } else if (profile.isInBrackets()) {
            Brackets brackets = profile.getBrackets();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lBrackets"));
            int pl = 0;
            for (int added = 0; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (brackets.getRemainingPlayers().size() <= pl) break;

                Player bracketsPlayer = brackets.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + bracketsPlayer.getName()));
            }
        } else if (profile.isInSumo()) {
            Sumo sumo = profile.getSumo();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lSumo"));
            int pl = 0;
            for (int added = 4; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (sumo.getRemainingPlayers().size() <= pl) break;

                Player sumoPlayer = sumo.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + sumoPlayer.getName()));
            }
        } else if (profile.isInLMS()) {
            LMS LMS = profile.getLms();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lFFA"));
            int pl = 0;
            for (int added = 4; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (LMS.getRemainingPlayers().size() <= pl) break;

                Player ffaPlayer = LMS.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + ffaPlayer.getName()));
            }
        } else if (profile.isInJuggernaut()) {
            Juggernaut juggernaut = profile.getJuggernaut();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lJuggernaut"));
            int pl = 0;
            for (int added = 4; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (juggernaut.getRemainingPlayers().size() <= pl) break;

                Player juggernautPlayer = juggernaut.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + juggernautPlayer.getName()));
            }
        } else if (profile.isInParkour()) {
            Parkour parkour = profile.getParkour();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lParkour"));
            int pl = 0;
            for (int added = 4; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (parkour.getRemainingPlayers().size() <= pl) break;

                Player parkourPlayer = parkour.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + parkourPlayer.getName()));
            }
        } else if (profile.isInWipeout()) {
            Wipeout wipeout = profile.getWipeout();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lWipeout"));
            int pl = 0;
            for (int added = 4; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (wipeout.getRemainingPlayers().size() <= pl) break;

                Player wipeoutPlayer = wipeout.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + wipeoutPlayer.getName()));
            }
        } else if (profile.isInSkyWars()) {
            SkyWars skyWars = profile.getSkyWars();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lSpleef"));
            int pl = 0;
            for (int added = 4; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (skyWars.getRemainingPlayers().size() <= pl) break;

                Player skyWarsPlayer = skyWars.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + skyWarsPlayer.getName()));
            }
        } else if (profile.isInSpleef()) {
            Spleef spleef = profile.getSpleef();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lSpleef"));
            int pl = 0;
            for (int added = 4; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (spleef.getRemainingPlayers().size() <= pl) break;

                Player spleefPlayer = spleef.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + spleefPlayer.getName()));
            }
        } else if (profile.isInInfected()) {
            Infected infected = profile.getInfected();
            tabObjects.add(new BufferedTabObject().slot(23).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.MAIN") + "&5&lInfected"));
            int pl = 0;
            for (int added = 4; added < 60; added++) {
                if (takenSlots.contains(added)) continue;

                if (infected.getRemainingPlayers().size() <= pl) break;

                Player infectedPlayer = infected.getRemainingPlayers().get(pl).getPlayer();
                pl++;
                tabObjects.add(new BufferedTabObject().slot(added).text(TechniquePlugin.get().getMainConfig().getString("TAB.COLORS.VALUES") + infectedPlayer.getName()));
            }
        }

        return tabObjects;
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


}
