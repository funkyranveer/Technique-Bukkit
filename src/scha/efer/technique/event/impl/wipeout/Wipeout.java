package scha.efer.technique.event.impl.wipeout;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.wipeout.player.WipeoutPlayer;
import scha.efer.technique.event.impl.wipeout.player.WipeoutPlayerState;
import scha.efer.technique.event.impl.wipeout.task.WipeoutRoundEndTask;
import scha.efer.technique.event.impl.wipeout.task.WipeoutRoundStartTask;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Wipeout {

    protected static String EVENT_PREFIX = CC.DARK_AQUA + CC.BOLD + "(Wipeout) " + CC.RESET;

    private final String name;
    @Setter
    private WipeoutState state = WipeoutState.WAITING;
    private WipeoutTask eventTask;
    private final PlayerSnapshot host;
    private final LinkedHashMap<UUID, WipeoutPlayer> eventPlayers = new LinkedHashMap<>();
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


    public Wipeout(Player player) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.maxPlayers = 100;
    }

    public List<String> getLore() {
        List<String> toReturn = new ArrayList<>();

        Wipeout wipeout = TechniquePlugin.get().getWipeoutManager().getActiveWipeout();

        toReturn.add(CC.MENU_BAR);
        toReturn.add(CC.translate("&5Host: &r" + wipeout.getName()));

        if (wipeout.isWaiting()) {
            toReturn.add("&5Players: &r" + wipeout.getEventPlayers().size() + "/" + wipeout.getMaxPlayers());
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
            toReturn.add("&5Remaining: &r" + wipeout.getRemainingPlayers().size() + "/" + wipeout.getTotalPlayers());
            toReturn.add("&5Duration: &r" + wipeout.getRoundDuration());
        }
        toReturn.add(CC.MENU_BAR);

        return toReturn;
    }

    public void setEventTask(WipeoutTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(TechniquePlugin.get(), 0L, 20L);
        }
    }

    public boolean isWaiting() {
        return state == WipeoutState.WAITING;
    }

    public boolean isFighting(Player player) {
        if (state.equals(WipeoutState.ROUND_FIGHTING)) {
            return getRemainingPlayers().contains(player);
        } else {
            return false;
        }
    }

    public WipeoutPlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (WipeoutPlayer wipeoutPlayer : eventPlayers.values()) {
            Player player = wipeoutPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();

        for (WipeoutPlayer wipeoutPlayer : eventPlayers.values()) {
            if (wipeoutPlayer.getState() == WipeoutPlayerState.WAITING) {
                Player player = wipeoutPlayer.getPlayer();
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public void handleJoin(Player player) {
        eventPlayers.put(player.getUniqueId(), new WipeoutPlayer(player));
        getRemainingPlayers().remove(player);

        broadcastMessage(CC.RED + player.getName() + CC.GRAY + " joined the wipeout " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setWipeout(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(TechniquePlugin.get().getWipeoutManager().getWipeoutSpawn());

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

        if (state == WipeoutState.WAITING) {
            broadcastMessage(CC.RED + player.getName() + CC.GRAY + " left the wipeout " + CC.GRAY +
                    "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setWipeout(null);
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
        TechniquePlugin.get().getWipeoutManager().setActiveWipeout(null);
        TechniquePlugin.get().getWipeoutManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        if (winner == null) {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The wipeout has been canceled.");
        } else {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the wipeout!");
        }

        for (WipeoutPlayer wipeoutPlayer : eventPlayers.values()) {
            Player player = wipeoutPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setWipeout(null);
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
                .parse(EVENT_PREFIX + CC.RED + getHost().getUsername() + CC.GRAY + " is hosting Wipeout " + CC.GRAY + "(Click to join)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GRAY + "Click to join the wipeout.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wipeout join"))
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
    }

    public void onRound() {
        setState(WipeoutState.ROUND_STARTING);

        for (Player player : this.getRemainingPlayers()) {
            if (player != null) {
                player.teleport(TechniquePlugin.get().getWipeoutManager().getWipeoutSpawn());

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInWipeout()) {
                    profile.refreshHotbar();
                }
                PlayerUtil.reset(player);
            }
        }
        setEventTask(new WipeoutRoundStartTask(this));
    }

    public void handleWin(Player player) {
        setState(WipeoutState.ROUND_ENDING);
        setEventTask(new WipeoutRoundEndTask(this, player));
    }

    public String getRoundDuration() {
        if (getState() == WipeoutState.ROUND_STARTING) {
            return "00:00";
        } else if (getState() == WipeoutState.ROUND_FIGHTING) {
            return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
        } else {
            return "Ending";
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setWipeout(this);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(TechniquePlugin.get().getWipeoutManager().getWipeoutSpawn());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setWipeout(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        TechniquePlugin.get().getEssentials().teleportToSpawn(player);
    }
}
