package scha.efer.technique.profile.hotbar;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Hotbar {

    @Getter
    private static final Map<HotbarItem, ItemStack> items = new HashMap<>();

    // Utility class - cannot be instantiated
    private Hotbar() {
    }

    public static void init() {
        items.put(HotbarItem.PLAY_GAME, new ItemBuilder(Material.STONE_SWORD)
                .name(CC.YELLOW + "Play Game" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.LEADERBOARDS, new ItemBuilder(Material.EMERALD)
                .name(CC.GOLD + "View Leaderboards" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.QUEUE_JOIN_UNRANKED, new ItemBuilder(Material.IRON_SWORD)
                .name(CC.YELLOW + "Unranked Queue" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.QUEUE_JOIN_RANKED, new ItemBuilder(Material.DIAMOND_SWORD)
                .name(CC.GREEN + "Ranked Queue" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.QUEUE_JOIN_CLAN, new ItemBuilder(Material.GOLD_SWORD)
                .name(CC.GOLD + "Clan Queue" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.JOIN_FFA, new ItemBuilder(Material.GOLD_AXE)
                .name(CC.PINK + "Join FFA" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.QUEUE_LEAVE, new ItemBuilder(Material.INK_SACK)
                .durability(1)
                .name(CC.RED + "Leave Queue" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PARTY_EVENTS, new ItemBuilder(Material.GOLD_AXE)
                .name(CC.BLUE + "Party Events" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PARTY_CREATE, new ItemBuilder(Material.NAME_TAG)
                .name(CC.DARK_GREEN + "Create Party" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PARTY_DISBAND, new ItemBuilder(Material.INK_SACK)
                .durability(1)
                .name(CC.RED + "Disband Party" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PARTY_SETTINGS, new ItemBuilder(Material.ANVIL)
                .name(CC.YELLOW + "Party Settings" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PARTY_LEAVE, new ItemBuilder(Material.INK_SACK)
                .durability(1)
                .name(CC.RED + "Leave Party" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PARTY_INFORMATION, new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .name(CC.GOLD + "Party Information" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PARTY_MEMBERS, new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .name(CC.GREEN + "Party Members" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.OTHER_PARTIES, new ItemBuilder(Material.CHEST)
                .name(CC.DARK_GREEN + "Other Parties" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.STATS_MENU, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.DARK_AQUA + "Profile" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.LEADERBOARDS_MENU, new ItemBuilder(Material.QUARTZ)
                .name(CC.GOLD + "Leaderboards" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.KIT_EDITOR, new ItemBuilder(Material.BOOK)
                .name(CC.DARK_PURPLE + "Kit Editor" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.SPECTATE_STOP, new ItemBuilder(Material.INK_SACK)
                .durability(1)
                .name(CC.RED + CC.BOLD + "Stop Spectating" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.VIEW_INVENTORY, new ItemBuilder(Material.BOOK)
                .name(CC.DARK_PURPLE + "View Inventory" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.EVENT_JOIN, new ItemBuilder(Material.EMERALD)
                .name(CC.RED + "Join Event" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.SUMO_LEAVE, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.RED + "Leave Sumo" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.BRACKETS_LEAVE, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.DARK_PURPLE + "Leave Brackets" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.FFA_LEAVE, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.RED + "Leave FFA" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.JUGGERNAUT_LEAVE, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.RED + "Leave Juggernaut" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PARKOUR_LEAVE, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.RED + "Leave Parkour" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.WIPEOUT_LEAVE, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.RED + "Leave Wipeout" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.SKYWARS_LEAVE, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.RED + "Leave SkyWars" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.SPLEEF_LEAVE, new ItemBuilder(Material.NETHER_STAR)
                .name(CC.RED + "Leave Spleef" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.REMATCH_REQUEST, new ItemBuilder(Material.BLAZE_POWDER)
                .name(CC.BLUE + "Request Rematch" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.DEFAULT_KIT, new ItemBuilder(Material.BOOK)
                .name(CC.DARK_PURPLE + "Default Kit" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.DIAMOND_KIT, new ItemBuilder(Material.DIAMOND_SWORD)
                .name(CC.DARK_PURPLE + "Diamond Kit" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.BARD_KIT, new ItemBuilder(Material.BLAZE_POWDER)
                .name(CC.DARK_PURPLE + "Bard Kit" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.ROGUE_KIT, new ItemBuilder(Material.GOLD_SWORD)
                .name(CC.DARK_PURPLE + "Rogue Kit" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.ARCHER_KIT, new ItemBuilder(Material.BOW)
                .name(CC.DARK_PURPLE + "Archer Kit" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.MATCH_END, new ItemBuilder(Material.INK_SACK)
                .durability(1)
                .name(CC.RED + "End the match!" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.PACK_SHOWCASE, new ItemBuilder(Material.PAINTING)
                .name(CC.DARK_PURPLE + "Pack Showcase" + CC.GRAY + " (Right-Click)")
                .build());
        items.put(HotbarItem.LEAVE_SHOWCASE, new ItemBuilder(Material.REDSTONE)
                .name(CC.RED + "Back to Spawn" + CC.GRAY + " (Right-Click)")
                .build());
    }

    public static ItemStack[] getLayout(HotbarLayout layout, Profile profile) {
        ItemStack[] toReturn = new ItemStack[9];
        Arrays.fill(toReturn, null);

        switch (layout) {
            case LOBBY: {
                if (profile.getParty() == null) {
                    boolean activeEvent = (TechniquePlugin.get().getSumoManager().getActiveSumo() != null && TechniquePlugin.get().getSumoManager().getActiveSumo().isWaiting())
                            || (TechniquePlugin.get().getBracketsManager().getActiveBrackets() != null && TechniquePlugin.get().getBracketsManager().getActiveBrackets().isWaiting())
                            || (TechniquePlugin.get().getLMSManager().getActiveLMS() != null && TechniquePlugin.get().getLMSManager().getActiveLMS().isWaiting())
                            || (TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut() != null && TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().isWaiting())
                            || (TechniquePlugin.get().getParkourManager().getActiveParkour() != null && TechniquePlugin.get().getParkourManager().getActiveParkour().isWaiting())
                            || (TechniquePlugin.get().getWipeoutManager().getActiveWipeout() != null && TechniquePlugin.get().getWipeoutManager().getActiveWipeout().isWaiting())
                            || (TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars() != null && TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars().isWaiting())
                            || (TechniquePlugin.get().getSpleefManager().getActiveSpleef() != null && TechniquePlugin.get().getSpleefManager().getActiveSpleef().isWaiting())
                            || (TechniquePlugin.get().getInfectedManager().getActiveInfected() != null && TechniquePlugin.get().getInfectedManager().getActiveInfected().isWaiting());


                    toReturn[0] = items.get(HotbarItem.PLAY_GAME);
                    toReturn[1] = items.get(HotbarItem.LEADERBOARDS);
                    /*toReturn[0] = items.get(HotbarItem.QUEUE_JOIN_UNRANKED);
                    toReturn[1] = items.get(HotbarItem.QUEUE_JOIN_RANKED);
                    toReturn[2] = items.get(HotbarItem.QUEUE_JOIN_CLAN);*/
                    toReturn[4] = items.get(HotbarItem.JOIN_FFA);
                    toReturn[5] = items.get(HotbarItem.PARTY_CREATE);

                    if (profile.getRematchData() != null) {
                        toReturn[2] = items.get(HotbarItem.REMATCH_REQUEST);
                    }
                    /*if (activeEvent) {
    					toReturn[4] = items.get(HotbarItem.PARTY_CREATE);
					} else {
                        toReturn[4] = items.get(HotbarItem.EVENT_JOIN);
						toReturn[5] = items.get(HotbarItem.PARTY_CREATE);
                    }*/

                    toReturn[7] = items.get(HotbarItem.STATS_MENU);
                    toReturn[8] = items.get(HotbarItem.KIT_EDITOR);
                } else {
                    if (profile.getParty().isLeader(profile.getUuid())) {
                        toReturn[0] = items.get(HotbarItem.PARTY_EVENTS);
                        toReturn[4] = items.get(HotbarItem.PARTY_MEMBERS);
                        toReturn[1] = items.get(HotbarItem.OTHER_PARTIES);
                        toReturn[7] = items.get(HotbarItem.PARTY_SETTINGS);
                        toReturn[8] = items.get(HotbarItem.PARTY_DISBAND);
                    } else {
                        toReturn[0] = items.get(HotbarItem.PARTY_MEMBERS);
                        toReturn[4] = items.get(HotbarItem.OTHER_PARTIES);
                        toReturn[8] = items.get(HotbarItem.PARTY_LEAVE);
                    }
                }
            }
            break;
            case ROOM:
                toReturn[8] = items.get(HotbarItem.LEAVE_SHOWCASE);

            break;
            case QUEUE: {
                toReturn[0] = items.get(HotbarItem.QUEUE_LEAVE);
            }
            break;
            case SUMO_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.SUMO_LEAVE);
            }
            break;
            case BRACKETS_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.BRACKETS_LEAVE);
            }
            break;
            case FFA_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.FFA_LEAVE);
            }
            break;
            case JUGGERNAUT_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.JUGGERNAUT_LEAVE);
            }
            break;
            case PARKOUR_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.PARKOUR_LEAVE);
            }
            break;
            case WIPEOUT_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.WIPEOUT_LEAVE);
            }
            break;
            case SKYWARS_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.SKYWARS_LEAVE);
            }
            break;
            case SPLEEF_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.SPLEEF_LEAVE);
            }
            break;
            case INFECTED_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.INFECTED_LEAVE);
            }
            break;
            case MATCH_SPECTATE: {
                toReturn[8] = items.get(HotbarItem.SPECTATE_STOP);
            }
            break;
        }

        return toReturn;
    }

    public static HotbarItem fromItemStack(ItemStack itemStack) {
        for (Map.Entry<HotbarItem, ItemStack> entry : Hotbar.getItems().entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(itemStack)) {
                return entry.getKey();
            }
        }

        return null;
    }

}
