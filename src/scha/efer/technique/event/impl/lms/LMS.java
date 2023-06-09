package scha.efer.technique.event.impl.lms;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.lms.player.LMSPlayer;
import scha.efer.technique.event.impl.lms.player.LMSPlayerState;
import scha.efer.technique.event.impl.lms.task.LMSRoundEndTask;
import scha.efer.technique.event.impl.lms.task.LMSRoundStartTask;
import scha.efer.technique.kit.Kit;
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
public class LMS {

    protected static String EVENT_PREFIX = CC.GOLD + "[LMS] " + CC.RESET;
    @Getter
    @Setter
    static private Kit kit;
    private final String name;
    @Setter
    private LMSState state = LMSState.WAITING;
    private LMSTask eventTask;
    private final PlayerSnapshot host;
    private final LinkedHashMap<UUID, LMSPlayer> eventPlayers = new LinkedHashMap<>();
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


    public LMS(Player player, Kit kit) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.maxPlayers = 100;
        LMS.kit = kit;
    }

    public List<String> getLore() {
        List<String> toReturn = new ArrayList<>();

        LMS LMS = TechniquePlugin.get().getLMSManager().getActiveLMS();

        toReturn.add(CC.MENU_BAR);
        toReturn.add(CC.translate("&fHost: &5" + LMS.getName()));
        toReturn.add(CC.translate("&fKit: &5" + kit.getName()));

        if (LMS.isWaiting()) {
            toReturn.add("&f* Players: &5" + LMS.getEventPlayers().size() + "/" + LMS.getMaxPlayers());
            toReturn.add("");

            if (LMS.getCooldown() == null) {
                toReturn.add(CC.translate("&fWaiting for players..."));
            } else {
                String remaining = TimeUtil.millisToSeconds(LMS.getCooldown().getRemaining());

                if (remaining.startsWith("-")) {
                    remaining = "0.0";
                }

                toReturn.add(CC.translate("&fThe match will begin in &5" + remaining + " &fseconds."));
            }
        } else {
            toReturn.add("&fRemaining: &5" + LMS.getRemainingPlayers().size() + "/" + LMS.getTotalPlayers());
            toReturn.add("&fDuration: &5" + LMS.getRoundDuration());
        }
        toReturn.add(CC.MENU_BAR);

        return toReturn;
    }

    public void setEventTask(LMSTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(TechniquePlugin.get(), 0L, 20L);
        }
    }

    public boolean isWaiting() {
        return state == LMSState.WAITING;
    }

    public boolean isFighting() {
        return state == LMSState.ROUND_FIGHTING;
    }

    public LMSPlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            Player player = LMSPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            if (LMSPlayer.getState() == LMSPlayerState.WAITING) {
                Player player = LMSPlayer.getPlayer();
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public void handleJoin(Player player) {
        eventPlayers.put(player.getUniqueId(), new LMSPlayer(player));

        broadcastMessage(CC.PINK + player.getName() + CC.YELLOW + " joined the event " + CC.WHITE + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(TechniquePlugin.get().getLMSManager().getLmsSpectator());

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
        if (state != LMSState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player, null);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == LMSState.WAITING) {
            broadcastMessage(CC.PINK + player.getName() + CC.YELLOW + " left the event " + CC.WHITE +
                    "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setLms(null);
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
    }

    protected List<Player> getSpectatorsList() {
        return PlayerUtil.convertUUIDListToPlayerList(spectators);
    }

    public void handleDeath(Player player, Player killer) {
        LMSPlayer loser = getEventPlayer(player);
        loser.setState(LMSPlayerState.ELIMINATED);

        onDeath(player, killer);
    }

    public void end() {
        TechniquePlugin.get().getLMSManager().setActiveLMS(null);
        TechniquePlugin.get().getLMSManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The FFA has been canceled.");
        } else {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(CC.PINK + CC.BOLD + "Last Man Standing Event");
            Bukkit.broadcastMessage(CC.YELLOW + "  Congratulations to " + CC.PINK + winner.getName() + CC.YELLOW + " for winning LMS event!");
            Bukkit.broadcastMessage(CC.GRAY + "");
        }

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            Player player = LMSPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setLms(null);
                profile.refreshHotbar();

                TechniquePlugin.get().getEssentials().teleportToSpawn(player);
            }
        }

        getSpectatorsList().forEach(this::removeSpectator);

        for (Player player : getPlayers()) {
            Profile.getByUuid(player.getUniqueId()).handleVisibility();
        }
    }

    public boolean canEnd() {
        int remaining = 0;

        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            if (LMSPlayer.getState() == LMSPlayerState.WAITING) {
                remaining++;
            }
        }

        return remaining == 1;
    }

    public Player getWinner() {
        for (LMSPlayer LMSPlayer : eventPlayers.values()) {
            if (LMSPlayer.getState() != LMSPlayerState.ELIMINATED) {
                return LMSPlayer.getPlayer();
            }
        }

        return null;
    }

    public void announce() {
        BaseComponent[] components = new ChatComponentBuilder("")
                .parse(EVENT_PREFIX + CC.PINK + getHost().getUsername() + CC.YELLOW + " is hosting a ffa event! " + CC.GREEN + "(Click to join)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GREEN + "Click to join.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lms join"))
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
        setState(LMSState.ROUND_STARTING);

        for (Player player : this.getRemainingPlayers()) {
            if (player != null) {
                player.teleport(TechniquePlugin.get().getLMSManager().getLmsSpectator());

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInLMS()) {
                    profile.refreshHotbar();
                }
                PlayerUtil.reset(player);
            }

            Profile.getByUuid(player.getUniqueId()).getKitData().get(getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
        }
        setEventTask(new LMSRoundStartTask(this));
    }

    public void onDeath(Player player, Player killer) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (killer != null) {
            broadcastMessage("&d" + player.getName() + "&e was eliminated by &d" + killer.getName() + "&e.");
        }


        if (canEnd()) {
            setState(LMSState.ROUND_ENDING);
            setEventTask(new LMSRoundEndTask(this));
        }

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

        new BukkitRunnable() {
            @Override
            public void run() {
                profile.refreshHotbar();
            }
        }.runTask(TechniquePlugin.get());
    }

    public String getRoundDuration() {
        if (getState() == LMSState.ROUND_STARTING) {
            return "00:00";
        } else if (getState() == LMSState.ROUND_FIGHTING) {
            return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
        } else {
            return "Ending";
        }
    }

    public boolean isFighting(Player player) {
        if (this.getState().equals(LMSState.ROUND_FIGHTING)) {
            return getRemainingPlayers().contains(player);
        } else {
            return false;
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(this);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(TechniquePlugin.get().getLMSManager().getLmsSpectator());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setLms(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        TechniquePlugin.get().getEssentials().teleportToSpawn(player);
    }
}
