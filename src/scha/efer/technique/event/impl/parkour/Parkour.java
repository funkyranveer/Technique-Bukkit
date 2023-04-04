package scha.efer.technique.event.impl.parkour;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.parkour.player.ParkourPlayer;
import scha.efer.technique.event.impl.parkour.player.ParkourPlayerState;
import scha.efer.technique.event.impl.parkour.task.ParkourRoundEndTask;
import scha.efer.technique.event.impl.parkour.task.ParkourRoundStartTask;
import scha.efer.technique.event.impl.parkour.task.ParkourWaterCheck;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.util.PlayerSnapshot;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ChatComponentBuilder;
import scha.efer.technique.util.external.Cooldown;
import scha.efer.technique.util.external.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Parkour {

    protected static String EVENT_PREFIX = CC.RED + CC.BOLD + "* " + CC.RESET;

    private final String name;
    @Setter
    private ParkourState state = ParkourState.WAITING;
    private ParkourTask eventTask;
    private final PlayerSnapshot host;
    private final LinkedHashMap<UUID, ParkourPlayer> eventPlayers = new LinkedHashMap<>();
    @Getter
    private final List<UUID> spectators = new ArrayList<>();
    private final int maxPlayers;
    @Getter
    @Setter
    private int totalPlayers;
    @Setter
    private Cooldown cooldown;
    @Setter
    private long roundStart;
    @Getter private BukkitRunnable parkourWaterCheck = new ParkourWaterCheck(this);


    public Parkour(Player player) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.maxPlayers = 100;
        parkourWaterCheck.runTaskTimer(TechniquePlugin.get(), 0, 20);
    }

    public List<String> getLore() {
        List<String> toReturn = new ArrayList<>();

        Parkour parkour = TechniquePlugin.get().getParkourManager().getActiveParkour();

        toReturn.add(CC.MENU_BAR);
        toReturn.add(CC.translate("&fHost: &5" + parkour.getName()));

        if (parkour.isWaiting()) {
            toReturn.add("&f* Players: &5" + parkour.getEventPlayers().size() + "/" + parkour.getMaxPlayers());
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
        toReturn.add(CC.MENU_BAR);

        return toReturn;
    }

    public void setEventTask(ParkourTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(TechniquePlugin.get(), 0L, 20L);
        }
    }

    public boolean isWaiting() {
        return state == ParkourState.WAITING;
    }

    public boolean isFighting(Player player) {
        if (this.getState().equals(ParkourState.ROUND_FIGHTING)) {
            return getRemainingPlayers().contains(player);
        } else {
            return false;
        }
    }

    public ParkourPlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (ParkourPlayer parkourPlayer : eventPlayers.values()) {
            Player player = parkourPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();

        for (ParkourPlayer parkourPlayer : eventPlayers.values()) {
            if (parkourPlayer.getState() == ParkourPlayerState.WAITING) {
                Player player = parkourPlayer.getPlayer();
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public void handleJoin(Player player) {
        eventPlayers.put(player.getUniqueId(), new ParkourPlayer(player));

        broadcastMessage(CC.PINK + player.getName() + CC.YELLOW + " joined the event " + CC.WHITE + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setParkour(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(TechniquePlugin.get().getParkourManager().getParkourSpawn());

        PlayerUtil.denyMovement(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(TechniquePlugin.get());
    }

    public void handleLeave(Player player) {
        eventPlayers.remove(player.getUniqueId());

        if (state == ParkourState.WAITING) {
            broadcastMessage(CC.RED + player.getName() + CC.GRAY + " left the event " + CC.RED +
                    "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setParkour(null);
        profile.refreshHotbar();

        TechniquePlugin.get().getEssentials().teleportToSpawn(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player otherPlayer : getPlayers()) {
                    Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                    otherProfile.handleVisibility(otherPlayer, player);
                    profile.handleVisibility(player, otherPlayer);
                }
            }
        }.runTaskAsynchronously(TechniquePlugin.get());

        if (getRemainingPlayers().size() == 1) {
            handleWin(getRemainingPlayers().get(0));
        }
    }

    protected List<Player> getSpectatorsList() {
        return PlayerUtil.convertUUIDListToPlayerList(spectators);
    }

    public void end(Player winner) {
        TechniquePlugin.get().getParkourManager().setActiveParkour(null);
        TechniquePlugin.get().getParkourManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        if (winner == null) {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The parkour has been canceled.");
            Bukkit.broadcastMessage(CC.GRAY + "");
        } else {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the parkour event!");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the parkour event!");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the parkour event!");
            Bukkit.broadcastMessage(CC.GRAY + "");
        }

        for (ParkourPlayer parkourPlayer : eventPlayers.values()) {
            Player player = parkourPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setParkour(null);
                profile.refreshHotbar();

                TechniquePlugin.get().getEssentials().teleportToSpawn(player);
            }
        }

        getSpectatorsList().forEach(this::removeSpectator);

        for (Player player : getPlayers()) {
            Profile.getByUuid(player.getUniqueId()).handleVisibility();
        }
    }

    public void announce() {
        BaseComponent[] components = new ChatComponentBuilder("")
                .parse(EVENT_PREFIX + CC.PINK + getHost().getUsername() + CC.YELLOW + " is hosting a parkour event! " + CC.GREEN + "(Click to join)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GREEN + "Click to join.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/parkour join"))
                .create();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!eventPlayers.containsKey(player.getUniqueId())) {
                player.sendMessage("");
                player.spigot().sendMessage(components);
                player.sendMessage("");
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(EVENT_PREFIX + CC.translate(message));
        }
    }

    public void onJoin(Player player) {
    }

    public void onLeave(Player player) {
        //player.setKnockbackProfile(null);
    }

    public void onRound() {
        setState(ParkourState.ROUND_STARTING);

        for (Player player : this.getRemainingPlayers()) {
            if (player != null) {
                player.teleport(TechniquePlugin.get().getParkourManager().getParkourSpawn());

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInParkour()) {
                    profile.refreshHotbar();
                }
                PlayerUtil.reset(player);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
            }
        }
        setEventTask(new ParkourRoundStartTask(this));
    }

    public void handleWin(Player player) {
        setState(ParkourState.ROUND_ENDING);
        setEventTask(new ParkourRoundEndTask(this, player));
        getPlayers().forEach(player1 -> player1.removePotionEffect(PotionEffectType.INVISIBILITY));
    }

    public String getRoundDuration() {
        if (getState() == ParkourState.ROUND_STARTING) {
            return "00:00";
        } else if (getState() == ParkourState.ROUND_FIGHTING) {
            return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
        } else {
            return "Ending";
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setParkour(this);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(TechniquePlugin.get().getParkourManager().getParkourSpawn());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setParkour(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        TechniquePlugin.get().getEssentials().teleportToSpawn(player);
    }
}
