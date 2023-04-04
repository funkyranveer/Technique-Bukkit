package scha.efer.technique.match.impl;

import gg.smok.knockback.KnockbackModule;
import gg.smok.knockback.KnockbackProfile;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanMatchHistory;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.MatchSnapshot;
import scha.efer.technique.match.MatchState;
import scha.efer.technique.match.task.MatchStartTask;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.profile.meta.ProfileMatchHistory;
import scha.efer.technique.profile.meta.ProfileRematchData;
import scha.efer.technique.queue.Queue;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.util.PacketUtils;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.TaskUtil;
import scha.efer.technique.util.elo.EloUtil;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ChatComponentBuilder;
import scha.efer.technique.util.nametag.NameTags;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class SumoMatch extends Match {

    @Setter
    private TeamPlayer playerA;
    @Setter
    private TeamPlayer playerB;

    public SumoMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, kit, arena, queueType);

        this.playerA = playerA;
        this.playerB = playerB;
    }

    @Override
    public boolean isSoloMatch() {
        return false;
    }

    @Override
    public boolean isMLGRushMatch() {
        return false;
    }

    @Override
    public boolean isSumoTeamMatch() {
        return false;
    }

    @Override
    public boolean isTeamMatch() {
        return false;
    }

    @Override
    public boolean isFreeForAllMatch() {
        return false;
    }

    @Override
    public boolean isHCFMatch() {
        return false;
    }

    @Override
    public boolean isKoTHMatch() {
        return false;
    }

    @Override
    public boolean isSumoMatch() {
        return true;
    }

    @Override
    public boolean isTheBridgeMatch() {
        return false;
    }

    @Override
    public boolean isBoxingMatch() {
        return false;
    }

    @Override
    public boolean isStickFightMatch() {
        return false;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) {
            return;
        }

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        PlayerUtil.denyMovement(player);

//		player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());

        if (!getKit().getGameRules().isNoitems()) {
            Profile.getByUuid(player.getUniqueId()).getKitData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
        }

        KnockbackProfile kbprofile = KnockbackModule.getDefault();
        if (getKit().getKnockbackProfile() != null && KnockbackModule.INSTANCE.profiles.containsKey(getKit().getKnockbackProfile())) {
            kbprofile = KnockbackModule.INSTANCE.profiles.get(getKit().getKnockbackProfile());
        }
        ((CraftPlayer) player).getHandle().setKnockback(kbprofile);

//		player.getInventory().setItem(8, Hotbar.getItems().get(HotbarItem.MATCH_END));

        Location spawn = playerA.equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();

        if (spawn.getBlock().getType() == Material.AIR) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.add(0, 2, 0));
        }

        NameTags.color(player, getOpponentPlayer(player), org.bukkit.ChatColor.DARK_PURPLE, getKit().getGameRules().isBuild());

    }

    @Override
    public void cleanPlayer(Player player) {

    }

    @Override
    public void onStart() {
    }

    @Override
    public boolean onEnd() {
        UUID rematchKey = UUID.randomUUID();

        for (TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()}) {
            if (!teamPlayer.isDisconnected() && teamPlayer.isAlive()) {
                Player player = teamPlayer.getPlayer();

                if (player != null) {
                    if (teamPlayer.isAlive()) {
                        MatchSnapshot snapshot = new MatchSnapshot(teamPlayer, getOpponentTeamPlayer(player));
                        getSnapshots().add(snapshot);
                    }
                }
            }
        }

        if (getKit().getGameRules().isTimed()) {
            TeamPlayer roundLoser = getTeamPlayer(getWinningPlayer());
            TeamPlayer roundWinner = getOpponentTeamPlayer(getOpponentPlayer(getWinningPlayer()));

            getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()}) {
                    if (!teamPlayer.isDisconnected()) {
                        Player player = teamPlayer.getPlayer();
                        Player opponent = getOpponentPlayer(player);

                        if (player != null) {
                            NameTags.reset(player, opponent);

                            player.setFireTicks(0);
                            player.updateInventory();

                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            TaskUtil.runSync(profile::refreshHotbar);
                            profile.handleVisibility();
                            KnockbackProfile kbprofile = KnockbackModule.getDefault();
                            ((CraftPlayer) player).getHandle().setKnockback(kbprofile);

                            if (opponent != null) {
                                profile.setRematchData(new ProfileRematchData(rematchKey, player.getUniqueId(),
                                        opponent.getUniqueId(), getKit(), getArena()));
                            }

                            TechniquePlugin.get().getEssentials().teleportToSpawn(player);
                        }
                    }
                }
            }
        }.runTaskLaterAsynchronously(TechniquePlugin.get(), (getKit().getGameRules().isWaterkill() || getKit().getGameRules().isSumo() || getKit().getGameRules().isLavakill() || getKit().getGameRules().isParkour()) ? 0L : 40L);

        Player winningPlayer = getWinningPlayer();
        Player losingPlayer = getOpponentPlayer(winningPlayer);

        TeamPlayer winningTeamPlayer = getTeamPlayer(winningPlayer);
        TeamPlayer losingTeamPlayer = getTeamPlayer(losingPlayer);

        PacketUtils.sendTitle(winningPlayer, CC.translate("&a&lVICTORY!"),
                CC.translate("&a" + winningPlayer.getName() + " &fwon the match!"), 20, 3 * 20, 20);

        PacketUtils.sendTitle(losingPlayer, CC.translate("&c&lDEFEAT!"),
                CC.translate("&a" + winningPlayer.getName() + " &fwon the match!"), 20, 3 * 20, 20);


        ChatComponentBuilder inventoriesBuilder = new ChatComponentBuilder("");

        inventoriesBuilder.append("  Winner: ").color(ChatColor.GREEN).append(winningPlayer.getName()).color(ChatColor.WHITE);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(winningTeamPlayer)).setCurrentClickEvent(getClickEvent(winningTeamPlayer)).append(" - ").color(ChatColor.GRAY).append("Loser: ").color(ChatColor.RED).append(losingPlayer.getName()).color(ChatColor.WHITE);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(losingTeamPlayer)).setCurrentClickEvent(getClickEvent(losingTeamPlayer));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse("&f" + CC.CIRCLE + " &d&lMatch Results &7(Click names to view)").create());
        components.add(inventoriesBuilder.create());


        Profile winningProfile = Profile.getByUuid(winningPlayer.getUniqueId());
        Profile losingProfile = Profile.getByUuid(losingPlayer.getUniqueId());

        if (getQueueType() == QueueType.UNRANKED) {
            winningProfile.getKitData().get(getKit()).incrementUnrankedWins();
            losingProfile.getKitData().get(getKit()).incrementUnrankedLost();

            ProfileMatchHistory winnerProfileMatchHistory = new ProfileMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), true, "UNRANKED", getKit().getName(), 0, 0, new Date(), winningProfile.getSumoRounds(), losingProfile.getSumoRounds());
            ProfileMatchHistory loserProfileMatchHistory = new ProfileMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), false, "UNRANKED", getKit().getName(), 0, 0, new Date(), winningProfile.getSumoRounds(), losingProfile.getSumoRounds());

            winningProfile.addMatchHistory(winnerProfileMatchHistory);
            losingProfile.addMatchHistory(loserProfileMatchHistory);
        }


        if (getQueueType() == QueueType.RANKED) {
            int oldWinnerElo = winningTeamPlayer.getElo();
            int oldLoserElo = losingTeamPlayer.getElo();
            int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);
            winningProfile.getKitData().get(getKit()).setElo(newWinnerElo);
            losingProfile.getKitData().get(getKit()).setElo(newLoserElo);
            winningProfile.getKitData().get(getKit()).incrementRankedWon();
            losingProfile.getKitData().get(getKit()).incrementRankedLost();
            winningProfile.calculateGlobalElo();
            losingProfile.calculateGlobalElo();

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            components.add(new ChatComponentBuilder("")
                    .parse("&fELO Changes: &a" + winningPlayer.getName() + " +" + winnerEloChange + " (" +
                            newWinnerElo + ") &5" + losingPlayer.getName() + " -" + loserEloChange + " (" + newLoserElo +
                            ")")
                    .create());

            ProfileMatchHistory winnerProfileMatchHistory = new ProfileMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), true, "RANKED", getKit().getName(), winnerEloChange, loserEloChange, new Date());
            ProfileMatchHistory loserProfileMatchHistory = new ProfileMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), false, "RANKED", getKit().getName(), winnerEloChange, loserEloChange, new Date());

            winningProfile.addMatchHistory(winnerProfileMatchHistory);
            losingProfile.addMatchHistory(loserProfileMatchHistory);
        }

        if (getQueueType() == QueueType.CLAN) {
            Clan winningClan = Clan.getByMember(winningPlayer.getUniqueId());
            Clan losingClan = Clan.getByMember(losingPlayer.getUniqueId());

            int oldWinnerElo = winningClan.getElo();
            int oldLoserElo = losingClan.getElo();
            int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);

            winningClan.setElo(newWinnerElo);
            losingClan.setElo(newLoserElo);

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            components.add(new ChatComponentBuilder("")
                    .parse("&eClan ELO Changes: &a" + winningClan.getName() + " +" + winnerEloChange + " (" +
                            newWinnerElo + ") &5" + losingClan.getName() + " -" + loserEloChange + " (" + newLoserElo +
                            ")")
                    .create());

            ClanMatchHistory winnerClanMatchHistory = new ClanMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), true, winnerEloChange);
            ClanMatchHistory loserClanMatchHistory = new ClanMatchHistory(getSnapshotOfPlayer(losingPlayer), getSnapshotOfPlayer(winningPlayer), false, loserEloChange);

            winningClan.addMatchHistory(winnerClanMatchHistory);
            losingClan.addMatchHistory(loserClanMatchHistory);


            winningClan.setWins(winningClan.getWins() + 1);
            losingClan.setLoses(losingClan.getLoses() + 1);
        }

        StringBuilder builder = new StringBuilder();

        if (!(getSpectators().size() <= 0)) {
            ArrayList<Player> specs = new ArrayList<>(getSpectators());
            int i = 0;
            for (Player spectator : getSpectators()) {
                Profile profile = Profile.getByUuid(spectator.getUniqueId());
                if (getSpectators().size() >= 1) {
                    if (profile.isSilent()) {
                        specs.remove(spectator);
                    } else {
                        if (!specs.contains(spectator))
                            specs.add(spectator);
                    }
                    if (i != getSpectators().size()) {
                        i++;
                        if (i == getSpectators().size()) {
                            if (!profile.isSilent()) {
                                builder.append(CC.GRAY).append(spectator.getName());
                            }
                        } else {
                            if (!profile.isSilent()) {
                                builder.append(CC.GRAY).append(spectator.getName()).append(CC.GRAY).append(", ");
                            }
                        }

                    }
                }
            }
            if (specs.size() >= 1) {
                components.add(new ChatComponentBuilder("").parse("&5Spectators (" + specs.size() + "): &f" + builder.substring(0, builder.length())).create());
            }
        }

        List<BaseComponent[]> chatbar = new ArrayList<>();
        chatbar.add(0, new ChatComponentBuilder("").parse(" ").create());

        for (Player player : new Player[]{winningPlayer, losingPlayer}) {
            chatbar.forEach(components1 -> player.spigot().sendMessage(components1));
            components.forEach(components1 -> player.spigot().sendMessage(components1));
            chatbar.forEach(components1 -> player.spigot().sendMessage(components1));
        }

        if (getMatchWaterCheck() != null) {
            getMatchWaterCheck().cancel();
        }

        winningProfile.setSumoRounds(0);
        losingProfile.setSumoRounds(0);

        return true;
    }

    @Override
    public boolean canEnd() {
        if (getRoundsNeeded(playerA) == 0 || getRoundsNeeded(playerB) == 0)
            return true;
        return playerA.isDisconnected() || playerB.isDisconnected();
    }

    @Override
    public Player getWinningPlayer() {
        if (getKit().getGameRules().isTimed()) {
            if (playerA.isDisconnected()) {
                return playerB.getPlayer();
            } else if (playerB.isDisconnected()) {
                return playerB.getPlayer();
            } else if (playerA.getHits() > playerB.getHits()) {
                return playerA.getPlayer();
            } else {
                return playerB.getPlayer();
            }
        } else if (getKit().getGameRules().isParkour()) {
            if (playerA.isDisconnected()) {
                return playerB.getPlayer();
            } else if (playerA.isAlive()) {
                return playerB.getPlayer();
            } else {
                return playerA.getPlayer();
            }
        } else {
            if (playerA.isDisconnected() || !playerA.isAlive()) {
                return playerB.getPlayer();
            } else {
                return playerA.getPlayer();
            }
        }
    }

    @Override
    public Team getWinningTeam() {
        throw new UnsupportedOperationException("Cannot get winning team from a SoloMatch");
    }

    @Override
    public TeamPlayer getTeamPlayerA() {
        return playerA;
    }

    @Override
    public TeamPlayer getTeamPlayerB() {
        return playerB;
    }

    @Override
    public List<TeamPlayer> getTeamPlayers() {
        return Arrays.asList(playerA, playerB);
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        Player playerA = this.playerA.getPlayer();

        if (playerA != null) {
            players.add(playerA);
        }

        Player playerB = this.playerB.getPlayer();

        if (playerB != null) {
            players.add(playerB);
        }

        return players;
    }

    @Override
    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();

        Player playerA = this.playerA.getPlayer();

        if (playerA != null) {
            players.add(playerA);
        }

        Player playerB = this.playerB.getPlayer();

        if (playerB != null) {
            players.add(playerB);
        }

        return players;
    }

    @Override
    public Team getTeamA() {
        throw new UnsupportedOperationException("Cannot get team from a SoloMatch");
    }

    @Override
    public Team getTeamB() {
        throw new UnsupportedOperationException("Cannot get team from a SoloMatch");
    }

    @Override
    public Team getTeam(Player player) {
        throw new UnsupportedOperationException("Cannot get team from a SoloMatch");
    }

    @Override
    public TeamPlayer getTeamPlayer(Player player) {
        if (playerA.getUuid().equals(player.getUniqueId())) {
            return playerA;
        } else if (playerB.getUuid().equals(player.getUniqueId())) {
            return playerB;
        } else {
            return null;
        }
    }

    @Override
    public Team getOpponentTeam(Team team) {
        throw new UnsupportedOperationException("Cannot get opponent team from a SoloMatch");
    }

    @Override
    public Team getOpponentTeam(Player player) {
        throw new UnsupportedOperationException("Cannot get opponent team from a SoloMatch");
    }

    @Override
    public Player getOpponentPlayer(Player player) {
        if (player == null) {
            return null;
        }

        if (playerA.getUuid().equals(player.getUniqueId())) {
            return playerB.getPlayer();
        } else if (playerB.getUuid().equals(player.getUniqueId())) {
            return playerA.getPlayer();
        } else {
            return null;
        }
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        if (playerA.getUuid().equals(player.getUniqueId())) {
            return playerB;
        } else if (playerB.getUuid().equals(player.getUniqueId())) {
            return playerA;
        } else {
            return null;
        }
    }

    @Override
    public int getTotalRoundWins() {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());
        return aProfile.getSumoRounds() + bProfile.getSumoRounds();
    }

    @Override
    public int getRoundsNeeded(TeamPlayer teamPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (playerA.equals(teamPlayer)) {
            return 3 - aProfile.getSumoRounds();
        } else if (playerB.equals(teamPlayer)) {
            return 3 - bProfile.getSumoRounds();
        } else {
            return -1;
        }
    }

    @Override
    public int getRoundsNeeded(Team team) {
        throw new UnsupportedOperationException("Cannot get team round wins from SoloMatch");
    }

    @Override
    public int getTeamACapturePoints() {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void setTeamACapturePoints(int number) {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public int getTeamBCapturePoints() {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void setTeamBCapturePoints(int number) {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public int getTimer() {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void setTimer(int number) {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public Player getCapper() {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void setCapper(Player player) {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (deadPlayer.isOnline()) {
            if (getRoundsNeeded(playerA) != 0 || getRoundsNeeded(playerB) != 0) {
                if (getWinningPlayer().getUniqueId().toString().equals(playerA.getUuid().toString())) {
                    aProfile.setSumoRounds(aProfile.getSumoRounds() + 1);
                } else if (getWinningPlayer().getUniqueId().toString().equals(playerB.getUuid().toString())) {
                    bProfile.setSumoRounds(bProfile.getSumoRounds() + 1);
                }

                getWinningPlayer().getPlayer().sendMessage(CC.translate("&aYou have won this round!"));
                getOpponentPlayer(getWinningPlayer()).getPlayer().sendMessage(CC.translate("&5You have lost this round!"));

                if (aProfile.getSumoRounds() >= 3 || bProfile.getSumoRounds() >= 3) {
                    TeamPlayer roundWinner = getTeamPlayer(getWinningPlayer());
                    TeamPlayer roundLoser = getOpponentTeamPlayer(getWinningPlayer());


                    getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));

                    PlayerUtil.reset(deadPlayer);

                    for (Player otherPlayer : getPlayersAndSpectators()) {
                        Profile profile = Profile.getByUuid(otherPlayer.getUniqueId());
                        profile.handleVisibility(otherPlayer, deadPlayer);
                    }

                    end();
                } else {
                    //kms
                    playerA.getPlayer().sendMessage(CC.translate("&fYou need to win &5" + getRoundsNeeded(playerA) + " &fmore rounds!"));
                    playerB.getPlayer().sendMessage(CC.translate("&fYou need to win &5" + getRoundsNeeded(playerB) + " &fmore rounds!"));

                    //Did it because I had to, kinda shitty that I had to though.
                    setupPlayer(playerA.getPlayer());
                    setupPlayer(playerB.getPlayer());


                    //Bug where both players couldn't see each other sometimes, fix for that is here
                    playerA.getPlayer().showPlayer(playerB.getPlayer());
                    playerB.getPlayer().showPlayer(playerA.getPlayer());

                    //Just in case.
                    onStart();
                    setState(MatchState.STARTING);
                    setStartTimestamp(-1);

                    //duh
                    new MatchStartTask(this).runTaskTimer(TechniquePlugin.get(), 20L, 20L);
                    getPlayers().forEach(player -> player.sendMessage(CC.YELLOW + "You are playing on arena " + CC.PINK + getArena().getName() + CC.YELLOW + "."));
                }
            }
        }
    }

    @Override
    public void onRespawn(Player player) {
        TechniquePlugin.get().getEssentials().teleportToSpawn(player);
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        if (playerA.getUuid().equals(viewer.getUniqueId()) || playerB.getUuid().equals(viewer.getUniqueId())) {
            return org.bukkit.ChatColor.DARK_PURPLE;
        } else {
            return org.bukkit.ChatColor.GREEN;
        }
    }
}
