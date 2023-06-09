package scha.efer.technique.event.impl.juggernaut;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.juggernaut.player.JuggernautPlayer;
import scha.efer.technique.event.impl.juggernaut.player.JuggernautPlayerState;
import scha.efer.technique.event.impl.juggernaut.task.JuggernautRoundEndTask;
import scha.efer.technique.event.impl.juggernaut.task.JuggernautRoundStartTask;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.util.PlayerSnapshot;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ChatComponentBuilder;
import scha.efer.technique.util.external.Cooldown;
import scha.efer.technique.util.external.TimeUtil;
import scha.efer.technique.util.nametag.NameTags;
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
public class Juggernaut {

    protected static String EVENT_PREFIX = CC.DARK_AQUA + CC.BOLD + "(Juggernaut) " + CC.RESET;

    private final String name;
    @Setter
    private JuggernautState state = JuggernautState.WAITING;
    private JuggernautTask eventTask;
    private final PlayerSnapshot host;
    @Getter
    @Setter
    private JuggernautPlayer juggernaut;
    private final LinkedHashMap<UUID, JuggernautPlayer> eventPlayers = new LinkedHashMap<>();
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


    public Juggernaut(Player player) {
        this.name = player.getName();
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.maxPlayers = 100;
    }

    public List<String> getLore() {
        List<String> toReturn = new ArrayList<>();

        Juggernaut juggernaut = TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut();

        toReturn.add(CC.MENU_BAR);
        toReturn.add(CC.translate("&5Host: &r" + juggernaut.getName()));

        if (juggernaut.isWaiting()) {
            toReturn.add("&5Players: &r" + juggernaut.getEventPlayers().size() + "/" + juggernaut.getMaxPlayers());
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
            toReturn.add("&5Juggernaut: &r" + juggernaut.getName());
            toReturn.add("&5Remaining: &r" + juggernaut.getRemainingPlayers().size() + "/" + juggernaut.getTotalPlayers());
            toReturn.add("&5Duration: &r" + juggernaut.getRoundDuration());
        }
        toReturn.add(CC.MENU_BAR);

        return toReturn;
    }

    public void setEventTask(JuggernautTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(TechniquePlugin.get(), 0L, 20L);
        }
    }

    public boolean isWaiting() {
        return state == JuggernautState.WAITING;
    }

    public boolean isFighting(Player player) {
        if (state.equals(JuggernautState.ROUND_FIGHTING)) {
            return getRemainingPlayers().contains(player);
        } else {
            return false;
        }
    }

    public JuggernautPlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
            Player player = juggernautPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public int getRandomPlayer() { //this works fine this is a shit :V
        int min = 0;
        int max = this.getRemainingPlayers().size() - 1;
        int r = (int) (Math.random() * (max - min)) + min;

        // new Random().nextInt( this.getRemainingPlayers().size()); :vvvvv

        return r;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();

        for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
            if (juggernautPlayer.getState() == JuggernautPlayerState.WAITING) {
                Player player = juggernautPlayer.getPlayer();
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public void handleJoin(Player player) {
        eventPlayers.put(player.getUniqueId(), new JuggernautPlayer(player));

        broadcastMessage(CC.RED + player.getName() + CC.GRAY + " joined the juggernaut " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

        onJoin(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setJuggernaut(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        player.teleport(TechniquePlugin.get().getJuggernautManager().getJuggernautSpectator());

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
        if (state != JuggernautState.WAITING) {
            if (isFighting(player)) {
                handleDeath(player, null);
            }
        }

        eventPlayers.remove(player.getUniqueId());

        if (state == JuggernautState.WAITING) {
            broadcastMessage(CC.RED + player.getName() + CC.GRAY + " left the juggernaut " + CC.GRAY +
                    "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
        }

        onLeave(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        profile.setJuggernaut(null);
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
        JuggernautPlayer loser = getEventPlayer(player);
        loser.setState(JuggernautPlayerState.ELIMINATED);

        onDeath(player, killer);
    }

    public void end(String whowins) {
        TechniquePlugin.get().getJuggernautManager().setActiveJuggernaut(null);
        TechniquePlugin.get().getJuggernautManager().setCooldown(new Cooldown(60_000L * 10));

        setEventTask(null);

        if (whowins.equalsIgnoreCase("None")) {
            Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + " the event is cancelled");
        } else {
            if (whowins.equalsIgnoreCase("The Juggernaut")) {
                Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + whowins + CC.GRAY + " (" + juggernaut.getPlayer().getName() + ")" + CC.GRAY + " has won the juggernaut!");
            } else {
                Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + whowins + CC.GRAY + " has won the juggernaut event!");
            }
        }

        for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
            Player player = juggernautPlayer.getPlayer();

            if (player != null) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.setState(ProfileState.IN_LOBBY);
                profile.setJuggernaut(null);
                profile.refreshHotbar();

                TechniquePlugin.get().getEssentials().teleportToSpawn(player);
            }
        }

        getSpectatorsList().forEach(this::removeSpectator);

        for (Player player : getPlayers()) {
            Profile.getByUuid(player.getUniqueId()).handleVisibility();
        }
    }

    public String canEnd() {
        List<JuggernautPlayer> p = new ArrayList<>();

        for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
            if (juggernautPlayer.getState() == JuggernautPlayerState.WAITING) {
                p.add(juggernautPlayer);
            }
        }

        boolean canEnd = true;

        if (p.size() == 1 && p.get(0).isJuggernaut()) {
            return "The Juggernaut";
        } else if (p.size() > 0) {
            for (JuggernautPlayer pl : p) {
                if (pl.isJuggernaut()) {
                    canEnd = false;
                }
            }
            if (canEnd) {
                return "Players";
            }
        }

        return "None";
    }

    public Player getWinner() {
        for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
            if (juggernautPlayer.getState() != JuggernautPlayerState.ELIMINATED) {
                return juggernautPlayer.getPlayer();
            }
        }

        return null;
    }

    public void announce() {
        BaseComponent[] components = new ChatComponentBuilder("")
                .parse(EVENT_PREFIX + CC.RED + getHost().getUsername() + CC.GRAY + " is hosting Juggernaut " + CC.GRAY + "(Click to join)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse(CC.GREEN + "Click to join the juggernaut.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/juggernaut join"))
                .create();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!eventPlayers.containsKey(player.getUniqueId())) {
                player.sendMessage(CC.translate(""));
                player.spigot().sendMessage(components);
                player.sendMessage(CC.translate(""));
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
        setState(JuggernautState.ROUND_STARTING);

        for (Player player : this.getRemainingPlayers()) {
            if (player != null) {
                player.teleport(TechniquePlugin.get().getJuggernautManager().getJuggernautSpectator());

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInJuggernaut()) {
                    profile.refreshHotbar();
                }
                PlayerUtil.reset(player);

                if (this.getEventPlayer(player).isJuggernaut()) {
                    Profile.getByUuid(player.getUniqueId()).getKitData().get(Kit.getByName("NoDebuff")).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
                } else {
                    Profile.getByUuid(player.getUniqueId()).getKitData().get(Kit.getByName("Soup")).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
                }

                if (this.getEventPlayer(player).isJuggernaut()) {
                    for (Player pl : this.getRemainingPlayers()) {
                        NameTags.color(player, pl, org.bukkit.ChatColor.DARK_PURPLE, true);
                    }
                } else {
                    for (Player pl : this.getRemainingPlayers()) {
                        if (getEventPlayer(pl).isJuggernaut()) {
                            NameTags.color(player, pl, org.bukkit.ChatColor.DARK_PURPLE, true);
                        } else {
                            NameTags.color(player, pl, org.bukkit.ChatColor.GREEN, true);
                        }
                    }
                }
            }
        }
        setEventTask(new JuggernautRoundStartTask(this));
    }

    public void onDeath(Player player, Player killer) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (killer != null) {
            broadcastMessage("&d" + player.getName() + "&e was eliminated by &d" + killer.getName() + "&e.");
        }


        if (!canEnd().equalsIgnoreCase("None")) {
            setState(JuggernautState.ROUND_ENDING);
            setEventTask(new JuggernautRoundEndTask(this));
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
        if (getState() == JuggernautState.ROUND_STARTING) {
            return "00:00";
        } else if (getState() == JuggernautState.ROUND_FIGHTING) {
            return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
        } else {
            return "Ending";
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setJuggernaut(this);
        profile.setState(ProfileState.SPECTATE_MATCH);
        profile.refreshHotbar();
        profile.handleVisibility();

        player.teleport(TechniquePlugin.get().getJuggernautManager().getJuggernautSpectator());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setJuggernaut(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();

        TechniquePlugin.get().getEssentials().teleportToSpawn(player);
    }
}
