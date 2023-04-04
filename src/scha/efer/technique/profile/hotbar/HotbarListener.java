package scha.efer.technique.profile.hotbar;

import org.bukkit.Sound;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.event.impl.brackets.Brackets;
import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.event.impl.lms.LMS;
import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.event.impl.skywars.SkyWars;
import scha.efer.technique.event.impl.spleef.Spleef;
import scha.efer.technique.event.impl.sumo.Sumo;
import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.event.menu.ActiveEventSelectEventMenu;
import scha.efer.technique.ffa.FFA;
import scha.efer.technique.kit.menu.KitEditorSelectKitMenu;
import scha.efer.technique.party.Party;
import scha.efer.technique.party.PartyMessage;
import scha.efer.technique.party.menu.ManagePartySettings;
import scha.efer.technique.party.menu.OtherPartiesMenu;
import scha.efer.technique.party.menu.PartyEventSelectEventMenu;
import scha.efer.technique.party.menu.PartyListMenu;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.profile.options.PlayerMenu;
import scha.efer.technique.profile.stats.menu.LeaderboardsMenu;
import scha.efer.technique.profile.stats.menu.RankedLeaderboardsMenu;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.queue.menu.QueueSelectKitMenu;
import scha.efer.technique.queue.menu.QueueSelectTypeMenu;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class HotbarListener implements Listener {


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            Player player = event.getPlayer();
            Profile profile = Profile.getByUuid(player.getUniqueId());

            HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

            if (hotbarItem == null) {
                return;
            }

            event.setCancelled(true);

            switch (hotbarItem) {
                case PLAY_GAME: {
                    if (!profile.isBusy(player)) {
                        new QueueSelectTypeMenu().openMenu(event.getPlayer());
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.CHEST_OPEN, 5F, 5F);
                    }
                }
                break;
                case LEADERBOARDS: {
                    new RankedLeaderboardsMenu().openMenu(player);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.CHEST_OPEN, 5F, 5F);
                }
                break;
                case QUEUE_JOIN_RANKED: {
                    if (!profile.isBusy(player)) {
                        new QueueSelectKitMenu(QueueType.RANKED).openMenu(event.getPlayer());
                    }
                }
                break;
                case QUEUE_JOIN_UNRANKED: {
                    if (!profile.isBusy(player)) {
                        new QueueSelectKitMenu(QueueType.UNRANKED).openMenu(event.getPlayer());
                    }
                }
                break;
                case QUEUE_JOIN_CLAN: {
                    if (!profile.isBusy(player)) {
                        Clan clan = Clan.getByMember(player.getUniqueId());
                        if (clan != null) {
                            new QueueSelectKitMenu(QueueType.CLAN).openMenu(event.getPlayer());
                        } else {
                            event.getPlayer().sendMessage(CC.RED + "You must have a clan to join clan queue. Do it right now via /clan.");
                        }
                    }
                }
                break;
                case QUEUE_LEAVE: {
                    if (profile.isInQueue()) {
                        profile.getQueue().removePlayer(profile.getQueueProfile());
                    }
                }
                break;
                case PARTY_EVENTS: {
                    new PartyEventSelectEventMenu().openMenu(player);
                }
                break;
                case OTHER_PARTIES: {
                    new OtherPartiesMenu().openMenu(event.getPlayer());
                }
                break;
                case PARTY_MEMBERS: {
                    new PartyListMenu().openMenu(event.getPlayer());
                }
                break;
                case PARTY_SETTINGS: {
                    new ManagePartySettings().openMenu(event.getPlayer());
                }
                break;
                case STATS_MENU: {
                    new PlayerMenu().openMenu(event.getPlayer());
                }
                break;
                case LEADERBOARDS_MENU: {
                    new LeaderboardsMenu().openMenu(event.getPlayer());
                }
                break;
                case KIT_EDITOR: {
                    if (profile.isInLobby() || profile.isInQueue()) {
                        new KitEditorSelectKitMenu().openMenu(event.getPlayer());
                    }
                }
                break;
                case MATCH_END: {
                    if (profile.isInMatch()) {
                        player.setHealth(0);
                    }
                }
                break;
                case LEAVE_SHOWCASE:
                    TechniquePlugin.get().getEssentials().teleportToSpawn(player);
                    profile.refreshHotbar();
                    break;
                case JOIN_FFA:
                    player.chat("/joinffa");
                    player.playSound(player.getLocation(), Sound.VILLAGER_YES, 5F, 5F);
                    break;
                case PARTY_CREATE: {
                    if (profile.getParty() != null) {
                        player.sendMessage(CC.RED + "You already have a party.");
                        return;
                    }

                    if (!profile.isInLobby()) {
                        player.sendMessage(CC.RED + "You must be in the lobby to create a party.");
                        return;
                    }

                    profile.setParty(new Party(player));
                    profile.refreshHotbar();

                    player.sendMessage(PartyMessage.CREATED.format());
                }
                break;
                case PARTY_DISBAND: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.RED + "You do not have a party.");
                        return;
                    }

                    if (!profile.getParty().isLeader(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not the leader of your party.");
                        return;
                    }

                    profile.getParty().disband();
                }
                break;
                case PARTY_INFORMATION: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.RED + "You do not have a party.");
                        return;
                    }

                    profile.getParty().sendInformation(player);
                }
                break;
                case PARTY_LEAVE: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.RED + "You do not have a party.");
                        return;
                    }

                    if (profile.getParty().getLeader().getUuid().equals(player.getUniqueId())) {
                        profile.getParty().disband();
                    } else {
                        profile.getParty().leave(player, false);
                    }
                }
                break;
                case EVENT_JOIN: {
                    new ActiveEventSelectEventMenu().openMenu(player);
                }
                break;
                case SUMO_LEAVE: {
                    Sumo activeSumo = TechniquePlugin.get().getSumoManager().getActiveSumo();

                    if (activeSumo == null) {
                        player.sendMessage(CC.RED + "There is no active sumo.");
                        return;
                    }

                    if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active sumo.");
                        return;
                    }

                    TechniquePlugin.get().getSumoManager().getActiveSumo().handleLeave(player);
                }
                break;
                case BRACKETS_LEAVE: {
                    Brackets activeBrackets = TechniquePlugin.get().getBracketsManager().getActiveBrackets();

                    if (activeBrackets == null) {
                        player.sendMessage(CC.RED + "There is no active brackets.");
                        return;
                    }

                    if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active brackets.");
                        return;
                    }

                    TechniquePlugin.get().getBracketsManager().getActiveBrackets().handleLeave(player);
                }
                break;
                case FFA_LEAVE: {
                    LMS activeLMS = TechniquePlugin.get().getLMSManager().getActiveLMS();

                    if (activeLMS == null) {
                        player.sendMessage(CC.RED + "There is no active Juggernaut.");
                        return;
                    }

                    if (!profile.isInLMS() || !activeLMS.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Juggernaut.");
                        return;
                    }

                    TechniquePlugin.get().getLMSManager().getActiveLMS().handleLeave(player);
                }
                break;
                case JUGGERNAUT_LEAVE: {
                    Juggernaut activeJuggernaut = TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut();

                    if (activeJuggernaut == null) {
                        player.sendMessage(CC.RED + "There is no active Juggernaut.");
                        return;
                    }

                    if (!profile.isInJuggernaut() || !activeJuggernaut.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Juggernaut.");
                        return;
                    }

                    TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().handleLeave(player);
                }
                break;
                case PARKOUR_LEAVE: {
                    Parkour activeParkour = TechniquePlugin.get().getParkourManager().getActiveParkour();

                    if (activeParkour == null) {
                        player.sendMessage(CC.RED + "There is no active Parkour.");
                        return;
                    }

                    if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Parkour.");
                        return;
                    }

                    TechniquePlugin.get().getParkourManager().getActiveParkour().handleLeave(player);
                }
                break;
                case WIPEOUT_LEAVE: {
                    Wipeout activeWipeout = TechniquePlugin.get().getWipeoutManager().getActiveWipeout();

                    if (activeWipeout == null) {
                        player.sendMessage(CC.RED + "There is no active Wipeout.");
                        return;
                    }

                    if (!profile.isInWipeout() || !activeWipeout.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Wipeout.");
                        return;
                    }

                    TechniquePlugin.get().getWipeoutManager().getActiveWipeout().handleLeave(player);
                }
                break;
                case SKYWARS_LEAVE: {
                    SkyWars activeSkyWars = TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars();

                    if (activeSkyWars == null) {
                        player.sendMessage(CC.RED + "There is no active SkyWars.");
                        return;
                    }

                    if (!profile.isInSkyWars() || !activeSkyWars.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active SkyWars.");
                        return;
                    }

                    TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars().handleLeave(player);
                }
                break;
                case SPLEEF_LEAVE: {
                    Spleef activeSpleef = TechniquePlugin.get().getSpleefManager().getActiveSpleef();

                    if (activeSpleef == null) {
                        player.sendMessage(CC.RED + "There is no active Spleef.");
                        return;
                    }

                    if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Spleef.");
                        return;
                    }

                    TechniquePlugin.get().getSpleefManager().getActiveSpleef().handleLeave(player);
                }
                break;
                case SPECTATE_STOP: {
                    if (profile.isInFight() && !profile.getMatch().getTeamPlayer(player).isAlive()) {
                        profile.getMatch().getTeamPlayer(player).setDisconnected(true);
                        profile.setState(ProfileState.IN_LOBBY);
                        profile.setMatch(null);
                    } else if (profile.isSpectating()) {
                        if (profile.getMatch() != null) {
                            profile.getMatch().removeSpectator(player);
                        } else if (profile.getSumo() != null) {
                            profile.getSumo().removeSpectator(player);
                        } else if (profile.getBrackets() != null) {
                            profile.getBrackets().removeSpectator(player);
                        } else if (profile.getLms() != null) {
                            profile.getLms().removeSpectator(player);
                        } else if (profile.getJuggernaut() != null) {
                            profile.getJuggernaut().removeSpectator(player);
                        } else if (profile.getParkour() != null) {
                            profile.getParkour().removeSpectator(player);
                        } else if (profile.getWipeout() != null) {
                            profile.getWipeout().removeSpectator(player);
                        } else if (profile.getSkyWars() != null) {
                            profile.getSkyWars().removeSpectator(player);
                        } else if (profile.getSpleef() != null) {
                            profile.getSpleef().removeSpectator(player);
                        }
                    } else {
                        player.sendMessage(CC.RED + "You are not spectating a match.");
                    }
                }
                break;
                case REMATCH_REQUEST:
                    profile.getPlayer().chat("/rematch");
                    break;
                default: {
                    return;
                }
            }
        }
    }

}
