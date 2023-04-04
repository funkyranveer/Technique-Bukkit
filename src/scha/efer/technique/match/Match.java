package scha.efer.technique.match;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.events.MatchEndEvent;
import scha.efer.technique.match.events.MatchStartEvent;
import scha.efer.technique.match.task.*;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.queue.Queue;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ChatComponentBuilder;
import scha.efer.technique.util.external.TimeUtil;
import scha.efer.technique.util.nametag.NameTags;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public abstract class Match {

    @Getter
    protected static List<Match> matches = new ArrayList<>();

    private final UUID matchId = UUID.randomUUID();
    private final Queue queue;
    private final Kit kit;
    private final Arena arena;
    private final QueueType queueType;
    public Map<UUID, EnderPearl> pearlMap = new HashMap<>();
    @Setter
    private MatchState state = MatchState.STARTING;
    private final List<MatchSnapshot> snapshots = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private final List<Location> placedBlocks = new ArrayList<>();
    private final List<BlockState> changedBlocks = new ArrayList<>();
    @Setter
    private long startTimestamp;

    @Getter
    @Setter
    private BukkitTask matchWaterCheck;

    public Match(Queue queue, Kit kit, Arena arena, QueueType queueType) {
        this.queue = queue;
        this.kit = kit;
        this.arena = arena;
        this.queueType = queueType;

        matches.add(this);
    }

    public static void init() {
        new MatchPearlCooldownTask().runTaskTimerAsynchronously(TechniquePlugin.get(), 2L, 2L);
        new MatchSnapshotCleanupTask().runTaskTimerAsynchronously(TechniquePlugin.get(), 20L * 5, 20L * 5);
    }

    public static void cleanup() {
        for (Match match : matches) {
            match.getPlacedBlocks().forEach(location -> location.getBlock().setType(Material.AIR));
            match.getChangedBlocks().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
            match.getEntities().forEach(Entity::remove);
        }
    }

    public static int getInFights(Queue queue) {
        int i = 0;

        for (Match match : matches) {
            if (match.getQueue() != null && (match.isFighting() || match.isStarting())) {
                if (match.getQueue() != null && match.getQueue().equals(queue)) {
                    i = i + match.getTeamPlayers().size();
                }
            }
        }

        return i;
    }

    public boolean isStarting() {
        return state == MatchState.STARTING;
    }

    public boolean isFighting() {
        return state == MatchState.FIGHTING;
    }

    public boolean isEnding() {
        return state == MatchState.ENDING;
    }

    public void start() {
        for (Player player : getPlayers()) {

            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setState(ProfileState.IN_FIGHT);

            profile.setMatch(this);

            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                profile.handleVisibility(player, otherPlayer);
            }

            setupPlayer(player);
            //todo: profile.getMatch().getTeamPlayerA().
        }

        onStart();

        for (Player player : this.getPlayers()) {
            if (!Profile.getByUuid(player.getUniqueId()).getSentDuelRequests().isEmpty()) {
                Profile.getByUuid(player.getUniqueId()).getSentDuelRequests().clear();
            }
        }

        state = MatchState.STARTING;
        startTimestamp = -1;
        arena.setActive(true);

        if (getKit() != null) {
            if (getKit().getGameRules().isWaterkill() || getKit().getGameRules().isParkour() || getKit().getGameRules().isSumo()) {
                matchWaterCheck = new MatchWaterCheckTask(this).runTaskTimer(TechniquePlugin.get(), 60L, 20L);
            }
        }

        new MatchStartTask(this).runTaskTimer(TechniquePlugin.get(), 20L, 20L);

        getPlayers().forEach(player -> player.sendMessage(CC.YELLOW + "You are playing on arena " + CC.PINK + arena.getName() + CC.YELLOW + "."));

        if (getKit().getName().equalsIgnoreCase("Boxing")) {
            getPlayers().forEach(player -> player.sendMessage("   "));
            getPlayers().forEach(player -> player.sendMessage(CC.DARK_PURPLE + CC.BOLD + "Boxing"));
            getPlayers().forEach(player -> player.sendMessage(CC.WHITE + " Reach 100 hits to win that match!"));
            getPlayers().forEach(player -> player.sendMessage("   "));
        } else if (getKit().getName().equalsIgnoreCase("MLG-Rush")) {
            getPlayers().forEach(player -> player.sendMessage("   "));
            getPlayers().forEach(player -> player.sendMessage(CC.DARK_PURPLE + CC.BOLD + "MLG Rush"));
            getPlayers().forEach(player -> player.sendMessage(CC.WHITE + " Break your opponent's bed three times and win the game!"));
            getPlayers().forEach(player -> player.sendMessage("   "));
        }


        getPlayers().forEach(player -> player.sendMessage(CC.translate("&c&lWARNING: &7Butterfly clicking is highly discouraged and may result in a ban! Use it under your own risk.")));

        for (Player shooter : getPlayers()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Profile shooterData = Profile.getByUuid(shooter.getUniqueId());

                    if (shooterData.isInFight()) {
                        int potions = 0;
                        for (ItemStack item : shooter.getInventory().getContents()) {
                            if (item == null)
                                continue;
                            if (item.getType() == Material.AIR)
                                continue;
                            if (item.getType() != Material.POTION)
                                continue;
                            if (item.getDurability() != (short) 16421)
                                continue;
                            potions++;
                        }
                        shooterData.getMatch().getTeamPlayer(shooter).setPotions(potions);
                    } else {
                        cancel();
                    }

                }
            }.runTaskTimerAsynchronously(TechniquePlugin.get(), 0L, 5L);
        }
        final MatchStartEvent event = new MatchStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void end() {
        if (onEnd()) {
            state = MatchState.ENDING;
        } else {
            return;
        }

        if (!isKoTHMatch()) {
            snapshots.forEach(matchInventory -> {
                matchInventory.setCreated(System.currentTimeMillis());
                MatchSnapshot.getSnapshots().put(matchInventory.getTeamPlayer().getUuid(), matchInventory);
            });
        }

        getPlayers().forEach(this::removePearl);

        getSpectators().forEach(this::removeSpectator);
        entities.forEach(Entity::remove);

        new MatchResetTask(this).runTask(TechniquePlugin.get());

        getArena().setActive(false);

        matches.remove(this);

        final MatchEndEvent event = new MatchEndEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        Bukkit.getScheduler().runTaskLaterAsynchronously(TechniquePlugin.get(), () -> getPlayers().forEach(player -> ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0)), 10L);
        Bukkit.getScheduler().runTaskLaterAsynchronously(TechniquePlugin.get(), () -> getPlayers().forEach(player -> ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0)), 20L);
    }

    public void handleRespawn(Player player) {
        player.setVelocity(new Vector());

        onRespawn(player);
    }

    public void onPearl(Player player, EnderPearl pearl) {
        this.pearlMap.put(player.getUniqueId(), pearl);
    }

    public void removePearl(Player player) {
        final EnderPearl pearl;
        if (player != null) {
            if ((pearl = this.pearlMap.remove(player.getUniqueId())) != null) {
                pearl.remove();
            }
        }
    }

    public void handleDeath(Player deadPlayer, Player killerPlayer, boolean disconnected) {
        TeamPlayer teamPlayer = this.getTeamPlayer(deadPlayer);

        if (getKit().getGameRules().isBridge()) {
            end();
            return;
        }

        if (getKit().getName().equalsIgnoreCase("MLG-Rush")) {
            end();
            return;
        }

        if (teamPlayer == null) return;

        teamPlayer.setDisconnected(disconnected);

        if (!teamPlayer.isAlive() && !isKoTHMatch()) {
            return;
        }

        teamPlayer.setAlive(false);

        List<Player> playersAndSpectators = getPlayersAndSpectators();


        for (Player player : playersAndSpectators) {
            if (teamPlayer.isDisconnected()) {
                player.sendMessage(getRelationColor(player, deadPlayer) + deadPlayer.getName() +
                        CC.GRAY + " has disconnected.");
                continue;
            }
            if ((!isHCFMatch() && !isKoTHMatch()) && getKit().getGameRules().isParkour()) {
                player.sendMessage(getRelationColor(player, deadPlayer) + deadPlayer.getName() +
                        CC.GRAY + " has won.");
            } else if (killerPlayer == null) {
                player.sendMessage(getRelationColor(player, deadPlayer) + deadPlayer.getName() +
                        CC.GRAY + " has died.");
            } else {
                if (killerPlayer.getName().equalsIgnoreCase("Stagflasyon") || deadPlayer.getName().equalsIgnoreCase("Stagflasyon")) {
                    player.sendMessage(CC.WHITE + deadPlayer.getName() +
                            CC.translate(" &4w&ca&6s &em&2u&ar&9d&3e&br&5e&dd &4b&cy ") + CC.WHITE +
                            killerPlayer.getName() + CC.WHITE + ".");
                } else {
                    player.sendMessage(CC.RED + deadPlayer.getName() +
                            CC.GRAY + " was murdered by " + CC.GREEN +
                            killerPlayer.getName() + CC.GRAY + ".");
                }
            }
        }

        onDeath(deadPlayer, killerPlayer);

        final Profile deadProfile = Profile.getByUuid(deadPlayer.getUniqueId());
        if (deadProfile.getOptions().isLightning()) {
            final PacketContainer lightningPacket = this.createLightningPacket(deadPlayer.getLocation());
            final float thunderSoundPitch = 0.8f + new Random().nextFloat() * 0.2f;
            for (final Player onlinePlayer : this.getPlayers()) {
                onlinePlayer.playSound(deadPlayer.getLocation(), Sound.AMBIENCE_THUNDER, 10000.0f, thunderSoundPitch);
                this.sendLightningPacket(onlinePlayer, lightningPacket);
            }
        }

        if ((isSumoMatch()) && disconnected || (isTheBridgeMatch()) && disconnected) {
            end();
            return;
        }

        if (!isKoTHMatch() && !isSumoMatch() && !isSumoTeamMatch() && !isTheBridgeMatch()) {
            if (canEnd()) {
                end();
            } else {
                PlayerUtil.spectator(deadPlayer);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : getPlayersAndSpectators()) {
                            Profile.getByUuid(player.getUniqueId()).handleVisibility(player, deadPlayer);
                        }
                    }
                }.runTaskLaterAsynchronously(TechniquePlugin.get(), 40L);
            }
        } else {
            if (canEnd()) {
                end();
            }
        }
    }

    private PacketContainer createLightningPacket(final Location location) {
        final PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);
        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128);
        lightningPacket.getIntegers().write(4, 1);
        lightningPacket.getIntegers().write(1, (int)(location.getX() * 32.0));
        lightningPacket.getIntegers().write(2, (int)(location.getY() * 32.0));
        lightningPacket.getIntegers().write(3, (int)(location.getZ() * 32.0));
        return lightningPacket;
    }

    private void sendLightningPacket(final Player target, final PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
        }
        catch (InvocationTargetException ex) {}
    }

    public String getDuration() {
        if (isStarting()) {
            return "00:00";
        } else if (isEnding()) {
            return "Ending";
        } else {
            return TimeUtil.millisToTimer(getElapsedDuration());
        }
    }

    public long getElapsedDuration() {
        return System.currentTimeMillis() - startTimestamp;
    }

    public void broadcastMessage(String message) {
        getPlayers().forEach(player -> player.sendMessage(message));
        getSpectators().forEach(player -> player.sendMessage(message));
    }

    public void broadcastSound(Sound sound) {
        getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
        getSpectators().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
    }

    protected List<Player> getSpectators() {
        return PlayerUtil.convertUUIDListToPlayerList(spectators);
    }

    public void addSpectator(Player player, Player target) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setMatch(this);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(target.getLocation().clone().add(0, 2, 0));
        player.spigot().setCollidesWithEntities(false);

        for (Player otherPlayer : getPlayers()) {
            if (!profile.isSilent()) {
                otherPlayer.sendMessage(CC.PINK + player.getName() + CC.YELLOW + " started spectating. " + CC.GRAY + "(" + spectators.size() + ")");
            }
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(TechniquePlugin.get(), () -> {
            if (this.isSoloMatch()) {
                NameTags.color(player, target, ChatColor.DARK_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth());
                NameTags.color(player, this.getOpponentPlayer(target), ChatColor.LIGHT_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth());
            }
            else if (this.isSumoMatch() || this.isTheBridgeMatch()) {
                NameTags.color(player, target, ChatColor.DARK_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth());
                NameTags.color(player, this.getOpponentPlayer(target), ChatColor.GREEN, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth());
            }
            else if (this.isTeamMatch()) {
                this.getTeam(target).getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.LIGHT_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
                this.getOpponentTeam(target).getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.DARK_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
            }
            else if (this.isSumoTeamMatch()) {
                this.getTeam(target).getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.LIGHT_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
                this.getOpponentTeam(target).getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.DARK_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
            }
            else if (this.isHCFMatch()) {
                this.getTeam(target).getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.LIGHT_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
                this.getOpponentTeam(target).getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.DARK_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
            }
            else if (this.isKoTHMatch()) {
                this.getTeam(target).getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.LIGHT_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
                this.getOpponentTeam(target).getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.DARK_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
            }
            else if (this.isFreeForAllMatch()) {
                this.getPlayers().forEach(p -> NameTags.color(player, p, ChatColor.DARK_PURPLE, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth()));
            }
        }, 20L);
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setMatch(null);
        profile.refreshHotbar();
        profile.handleVisibility();

        TechniquePlugin.get().getEssentials().teleportToSpawn(player);

        player.spigot().setCollidesWithEntities(true);

        if (state != MatchState.ENDING) {
            for (Player otherPlayer : getPlayers()) {
                if (!profile.isSilent()) {
                    otherPlayer.sendMessage(CC.PINK + player.getName() + CC.YELLOW + " stopped spectating. " + CC.GRAY + "(" + spectators.size() + ")");
                }
            }
        }
    }

    public List<Player> getPlayersAndSpectators() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(getPlayers());
        allPlayers.addAll(getSpectators());
        return allPlayers;
    }

    protected HoverEvent getHoverEvent(TeamPlayer teamPlayer) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                .parse("&eClick to view &d" + teamPlayer.getUsername() + "&e's inventory.").create());
    }

    protected ClickEvent getClickEvent(TeamPlayer teamPlayer) {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinv " + teamPlayer.getUuid().toString());
    }

    public abstract boolean isSoloMatch();

    public abstract boolean isSumoTeamMatch();

    public abstract boolean isTeamMatch();

    public abstract boolean isFreeForAllMatch();

    public abstract boolean isHCFMatch();

    public abstract boolean isKoTHMatch();

    public abstract boolean isSumoMatch();

    public abstract boolean isTheBridgeMatch();

    public abstract boolean isBoxingMatch();

    public abstract boolean isStickFightMatch();

    public abstract boolean isMLGRushMatch();

    public abstract void setupPlayer(Player player);

    public abstract void cleanPlayer(Player player);

    public abstract void onStart();

    public abstract boolean onEnd();

    public abstract boolean canEnd();

    public abstract void onDeath(Player player, Player killer);

    public abstract void onRespawn(Player player);

    public abstract Player getWinningPlayer();

    public abstract Team getWinningTeam();

    public abstract TeamPlayer getTeamPlayerA();

    public abstract TeamPlayer getTeamPlayerB();

    public abstract List<TeamPlayer> getTeamPlayers();

    public abstract List<Player> getPlayers();

    public abstract List<Player> getAlivePlayers();

    public abstract Team getTeamA();

    public abstract Team getTeamB();

    public abstract Team getTeam(Player player);

    public abstract TeamPlayer getTeamPlayer(Player player);

    public abstract Team getOpponentTeam(Team Team);

    public abstract Team getOpponentTeam(Player player);

    public abstract TeamPlayer getOpponentTeamPlayer(Player player);

    public abstract Player getOpponentPlayer(Player player);

    public abstract int getTotalRoundWins();

    public abstract int getRoundsNeeded(TeamPlayer teamPlayer);

    public abstract int getRoundsNeeded(Team Team);

    public abstract int getTeamACapturePoints();

    public abstract void setTeamACapturePoints(int number);

    public abstract int getTeamBCapturePoints();

    public abstract void setTeamBCapturePoints(int number);

    public abstract int getTimer();

    public abstract void setTimer(int number);

    public abstract Player getCapper();

    public abstract void setCapper(Player player);

    public abstract ChatColor getRelationColor(Player viewer, Player target);

    public MatchSnapshot getSnapshotOfPlayer(Player player) {
        for (MatchSnapshot snapshot : getSnapshots()) {
            if (snapshot.getTeamPlayer().getUuid().toString().equals(player.getUniqueId().toString())) {
                return snapshot;
            }
        }
        return null;
    }
}