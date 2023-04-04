package scha.efer.technique.match.impl;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.MatchSnapshot;
import scha.efer.technique.match.MatchState;
import scha.efer.technique.match.task.MatchStartTask;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.profile.meta.ProfileRematchData;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.queue.Queue;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.TaskUtil;
import scha.efer.technique.util.elo.EloUtil;
import scha.efer.technique.util.essentials.Essentials;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.chat.util.ChatComponentBuilder;
import scha.efer.technique.util.nametag.NameTags;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class TheBridgeMatch extends Match {

    @Setter private TeamPlayer playerA;
    @Setter private TeamPlayer playerB;
    @Getter private final List<Player> bridgePlayers = new ArrayList<>();

    public TheBridgeMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
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
        return false;
    }

    @Override
    public boolean isTheBridgeMatch() {
        return true;
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

        player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));

        Location spawn = playerA.equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();

        if (spawn.getBlock().getType() == Material.AIR) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.add(0, 2, 0));
        }
        teamPlayer.setPlayerSpawn(spawn);

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(null);
        player.getInventory().clear();

        player.getInventory().setArmorContents(getKit().getKitLoadout().getArmor());
        player.getInventory().setContents(getKit().getKitLoadout().getContents());
        giveBridgeKit(player);
        player.updateInventory();
        NameTags.color(player, getOpponentPlayer(player), org.bukkit.ChatColor.RED, getKit().getGameRules().isBuild());

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
                            NameTags.reset(player, teamPlayer.getPlayer());
                            TaskUtil.runSync(profile::refreshHotbar);
                            profile.handleVisibility();
                            //Array.getInstance().getKnockbackManager().getKnockbackType().applyDefaultKnockback(player);

                            if (opponent != null) {
                                profile.setRematchData(new ProfileRematchData(rematchKey, player.getUniqueId(),
                                        opponent.getUniqueId(), getKit(), getArena()));
                            }

                            Essentials.teleportToSpawn(player);
                        }
                    }
                }
            }
        }.runTaskLaterAsynchronously(TechniquePlugin.get(), (getKit().getGameRules().isWaterkill() || getKit().getGameRules().isSumo() || getKit().getGameRules().isLavakill() || getKit().getGameRules().isParkour()) ? 0L : 40L);
        ChatComponentBuilder inventoriesBuilder = new ChatComponentBuilder("");
        Player winningPlayer = getWinningPlayer();
        Player losingPlayer = getOpponentPlayer(winningPlayer);

        TeamPlayer winningTeamPlayer = getTeamPlayer(winningPlayer);
        TeamPlayer losingTeamPlayer = getTeamPlayer(losingPlayer);


        inventoriesBuilder.append("Winner: ").color(ChatColor.GREEN).append(winningPlayer.getName()).color(ChatColor.WHITE);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(winningTeamPlayer)).setCurrentClickEvent(getClickEvent(winningTeamPlayer)).append(" - ").color(ChatColor.GRAY).append("Loser: ").color(ChatColor.RED).append(losingPlayer.getName()).color(ChatColor.WHITE);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(losingTeamPlayer)).setCurrentClickEvent(getClickEvent(losingTeamPlayer));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse("&b&lMatch Details &7(Click name to view inventory)").create());
        components.add(inventoriesBuilder.create());


        Profile winningProfile = Profile.getByUuid(winningPlayer.getUniqueId());
        Profile losingProfile = Profile.getByUuid(losingPlayer.getUniqueId());

        if (getQueueType() == QueueType.UNRANKED) {
            //winningProfile.get.get(getKit()).incrementWon();
            //losingProfile.getStatisticsData().get(getKit()).incrementLost();
        }


        if (getQueueType() == QueueType.RANKED) {
            int oldWinnerElo = winningTeamPlayer.getElo();
            int oldLoserElo = losingTeamPlayer.getElo();
            int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);
            winningProfile.calculateGlobalElo();
            losingProfile.calculateGlobalElo();

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            components.add(new ChatComponentBuilder("")
                    .parse("&a" + winningPlayer.getName() + " +" + winnerEloChange + " (" +
                            newWinnerElo + ") &7⎜ &c" + losingPlayer.getName() + " -" + loserEloChange + " (" + newLoserElo +
                            ")")
                    .create());
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
                components.add(new ChatComponentBuilder("").parse("&5Spectators (" + specs.size() + "): &7" + builder.substring(0, builder.length())).create());
            }
        }

        List<BaseComponent[]> CHAT_BAR = new ArrayList<>();
        CHAT_BAR.add(0, new ChatComponentBuilder("").parse(CC.GRAY + CC.STRIKE_THROUGH + "------------------------------------------------").create());

        for (Player player : new Player[]{winningPlayer, losingPlayer}) {
            CHAT_BAR.forEach(components1 -> player.spigot().sendMessage(components1));
            components.forEach(components1 -> player.spigot().sendMessage(components1));
            CHAT_BAR.forEach(components1 -> player.spigot().sendMessage(components1));
        }

        winningProfile.setBridgeRounds(0);
        losingProfile.setBridgeRounds(0);
        return true;
    }

    @Override
    public boolean canEnd() {
        if (getRoundsNeeded(playerA) == 0 || getRoundsNeeded(playerB) == 0)
            return true;
        return playerA.isDisconnected() || playerB.isDisconnected();
    }

    @SneakyThrows
    public void playFirework() {
        final FireworkEffect effect=FireworkEffect.builder().withColor(Color.BLUE).with(FireworkEffect.Type.BALL_LARGE).build();
            //new FireworkEffectPlayer().playFirework(players.getWorld(), players.getLocation(), effect);
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
        throw new UnsupportedOperationException("Cannot getInstance winning team from a SoloMatch");
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
        throw new UnsupportedOperationException("Cannot getInstance team from a SoloMatch");
    }

    @Override
    public Team getTeamB() {
        throw new UnsupportedOperationException("Cannot getInstance team from a SoloMatch");
    }

    @Override
    public Team getTeam(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance team from a SoloMatch");
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
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a SoloMatch");
    }

    @Override
    public Team getOpponentTeam(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a SoloMatch");
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
        return aProfile.getBridgeRounds() + bProfile.getBridgeRounds();
    }

    @Override
    public int getRoundsNeeded(TeamPlayer teamPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (playerA.equals(teamPlayer)) {
            return 3 - aProfile.getBridgeRounds();
        } else if (playerB.equals(teamPlayer)) {
            return 3 - bProfile.getBridgeRounds();
        } else {
            return -1;
        }
    }

    @Override
    public int getRoundsNeeded(Team team) {
        throw new UnsupportedOperationException("Cannot getInstance team round wins from SoloMatch");
    }

    @Override
    public int getTeamACapturePoints() {
        return 0;
    }

    @Override
    public void setTeamACapturePoints(int number) {

    }

    @Override
    public int getTeamBCapturePoints() {
        return 0;
    }

    @Override
    public void setTeamBCapturePoints(int number) {

    }

    @Override
    public int getTimer() {
        return 0;
    }

    @Override
    public void setTimer(int number) {

    }

    @Override
    public Player getCapper() {
        return null;
    }

    @Override
    public void setCapper(Player player) {

    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (deadPlayer.isOnline()) {
            if (getRoundsNeeded(playerA) != 0 || getRoundsNeeded(playerB) != 0) {
                if (getWinningPlayer().getUniqueId().toString().equals(playerA.getUuid().toString())) {
                    aProfile.setBridgeRounds(aProfile.getBridgeRounds() + 1);
                } else if (getWinningPlayer().getUniqueId().toString().equals(playerB.getUuid().toString())) {
                    bProfile.setBridgeRounds(bProfile.getBridgeRounds() + 1);
                }

                getWinningPlayer().getPlayer().sendMessage(CC.translate("&aYou have won this round!"));
                getOpponentPlayer(getWinningPlayer()).getPlayer().sendMessage(CC.translate("&cYou have lost this round!"));

                if (aProfile.getBridgeRounds() >= 3 || bProfile.getBridgeRounds() >= 3) {
                    TeamPlayer roundWinner=getTeamPlayer(getWinningPlayer());
                    TeamPlayer roundLoser=getOpponentTeamPlayer(getWinningPlayer());

                    getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));

                    PlayerUtil.reset(deadPlayer);

                    for ( Player otherPlayer : getPlayersAndSpectators() ) {
                        Profile profile=Profile.getByUuid(otherPlayer.getUniqueId());
                        profile.handleVisibility(otherPlayer, deadPlayer);
                    }
                    end();
                } else {
                    //Send Round Message
                    playerA.getPlayer().sendMessage(CC.translate("&fYou need to win &5" + getRoundsNeeded(playerA) + " &fmore rounds!"));
                    playerB.getPlayer().sendMessage(CC.translate("&fYou need to win &5" + getRoundsNeeded(playerB) + " &fmore rounds!"));

                    //Setup Both Players
                    setupPlayer(playerA.getPlayer());
                    setupPlayer(playerB.getPlayer());

                    //Show The Players if their visibility is fucked
                    playerA.getPlayer().showPlayer(playerB.getPlayer());
                    playerB.getPlayer().showPlayer(playerA.getPlayer());
                    //Restart the Match
                    onStart();
                    setState(MatchState.STARTING);
                    setStartTimestamp(-1);
                    new MatchStartTask(this).runTaskTimer(TechniquePlugin.get(), 20L, 20L);
                }
            }
        }
    }

    @Override
    public void onRespawn(Player player) {
        Essentials.teleportToSpawn(player);
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        if (playerA.getUuid().equals(viewer.getUniqueId()) || playerB.getUuid().equals(viewer.getUniqueId())) {
            return org.bukkit.ChatColor.RED;
        } else {
            return org.bukkit.ChatColor.BLUE;
        }
    }

    public static void giveBridgeKit(Player player){
        Profile profile = Profile.getByUuid(player.getUniqueId());
        TheBridgeMatch teamMatch = (TheBridgeMatch) profile.getMatch();
        ItemStack[] armorRed = leatherArmor(Color.RED);
        ItemStack[] armorBlue = leatherArmor(Color.BLUE);
        if(teamMatch.getTeamPlayerA().getPlayer() == player){
            player.getInventory().setArmorContents(armorRed);
            player.getInventory().all(Material.STAINED_CLAY).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build());
            });
        }else{
            player.getInventory().setArmorContents(armorBlue);
            player.getInventory().all(Material.STAINED_CLAY).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build());
            });
        }
        player.updateInventory();
    }

    public static ItemStack[] leatherArmor(Color color){
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS).color(color).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).color(color).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(color).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(color).build()
        };
    }
}