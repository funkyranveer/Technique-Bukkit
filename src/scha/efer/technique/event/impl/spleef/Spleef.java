package scha.efer.technique.event.impl.spleef;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.spleef.player.SpleefPlayer;
import scha.efer.technique.event.impl.spleef.player.SpleefPlayerState;
import scha.efer.technique.event.impl.spleef.task.SpleefRoundEndTask;
import scha.efer.technique.event.impl.spleef.task.SpleefRoundStartTask;
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
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Spleef {

    protected static String EVENT_PREFIX = CC.BLUE + "[Spleef] " + CC.RESET;
    @Getter
    @Setter
    static private Kit kit = Kit.getByName("Spleef");
    private final String name;
    @Setter
    private SpleefState state = SpleefState.WAITING;
    private SpleefTask eventTask;
    private final PlayerSnapshot host;
    private final LinkedHashMap<UUID, SpleefPlayer> eventPlayers = new LinkedHashMap<>();
    @Getter
    private final List<UUID> spectators = new ArrayList<>();
    private final List<Location> placedBlocks = new ArrayList<>();
    private final List<BlockState> changedBlocks = new ArrayList<>();
    private final int maxPlayers;
    @Getter
    @Setter
    private int totalPlayers;
    @Setter
    private Cooldown cooldown;
    @Setter
    private long roundStart;


    public Spleef(Player player) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.maxPlayers = 100;
    }

    public List<String> getLore() {
        List<String> toReturn = new ArrayList<>();

        Spleef spleef = TechniquePlugin.get().getSpleefManager().getActiveSpleef();

        toReturn.add(CC.MENU_BAR);
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
        toReturn.add(CC.MENU_BAR);

        return toReturn;
    }

    public void setEventTask(SpleefTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(TechniquePlugin.get(), 0L, 20L);
        }
    }

    public boolean isWaiting() {
        return state == SpleefState.WAITING;
    }

    public boolean isFighting() {
        return state == SpleefState.ROUND_FIGHTING;
    }

    public boolean isFighting(Player player) {
        if (state.equals(SpleefState.ROUND_FIGHTING)) {
            return getRemainingPlayers().contains(player);
        } else {
            return false;
        }
    }

    public SpleefPlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
            Player player = spleefPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();

        for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
            if (spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                Player player = spleefPlayer.getPlayer();
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public void handleJoin(Player player) {
        eventPlayers.put(player.getUniqueId(), new SpleefPlayer(player));

        broadcastMessage(CC.PINK + player.getName() + CC.YELLOW + " joined the event " + CC.WHITE + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setSpleef(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(TechniquePlugin.get().getSpleefManager().getSpleefSpectator());

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
        if (state != SpleefState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == SpleefState.WAITING) {
            broadcastMessage(CC.PINK + player.getName() + CC.YELLOW + " left the event " + CC.WHITE +
                    "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setSpleef(null);
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

    public void handleDeath(Player player) {
        SpleefPlayer loser = getEventPlayer(player);
        loser.setState(SpleefPlayerState.ELIMINATED);

        onDeath(player);
    }

    public void end() {
        TechniquePlugin.get().getSpleefManager().setActiveSpleef(null);
        TechniquePlugin.get().getSpleefManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        new SpleefResetTask(this).runTask(TechniquePlugin.get());

        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The spleef has been canceled.");
            Bukkit.broadcastMessage(CC.GRAY + "");
        } else {
            Bukkit.broadcastMessage(CC.GRAY + "");
            Bukkit.broadcastMessage(CC.PINK + CC.BOLD + "Spleef Event");
            Bukkit.broadcastMessage(CC.YELLOW + "  Congratulations to " + CC.PINK + winner.getName() + CC.YELLOW + " for winning spleef event!");
            Bukkit.broadcastMessage(CC.GRAY + "");
        }

        for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
            Player player = spleefPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setSpleef(null);
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

        for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
            if (spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                remaining++;
            }
        }

        return remaining <= 1;
    }

    public Player getWinner() {
        for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
            if (spleefPlayer.getState() != SpleefPlayerState.ELIMINATED) {
                return spleefPlayer.getPlayer();
            }
        }

        return null;
    }

    public void announce() {
        BaseComponent[] components = new ChatComponentBuilder("")
                .parse(EVENT_PREFIX + CC.PINK + getHost().getUsername() + CC.YELLOW + " is hosting a spleef event! " + CC.GREEN + "(Click to join)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GREEN + "Click to join.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spleef join"))
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
        setState(SpleefState.ROUND_STARTING);

        int i = 0;
        for (Player player : this.getRemainingPlayers()) {
            if (player != null) {
                player.teleport(TechniquePlugin.get().getSpleefManager().getSpleefSpectator());
                i++;

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInSpleef()) {
                    profile.refreshHotbar();
                }
                PlayerUtil.reset(player);
            }

            Profile.getByUuid(player.getUniqueId()).getKitData().get(getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));

        }
        setEventTask(new SpleefRoundStartTask(this));
    }

    public void onDeath(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        broadcastMessage("&d" + player.getName() + "&e died!");


        if (canEnd()) {
            setState(SpleefState.ROUND_ENDING);
            setEventTask(new SpleefRoundEndTask(this));
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
        if (getState() == SpleefState.ROUND_STARTING) {
            return "00:00";
        } else if (getState() == SpleefState.ROUND_FIGHTING) {
            return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
        } else {
            return "Ending";
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setSpleef(this);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(TechniquePlugin.get().getSpleefManager().getSpleefSpectator());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setSpleef(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        TechniquePlugin.get().getEssentials().teleportToSpawn(player);
    }
}
