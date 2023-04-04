package scha.efer.technique.event.menu;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.EventType;
import scha.efer.technique.event.impl.brackets.command.BracketsJoinCommand;
import scha.efer.technique.event.impl.infected.command.InfectedJoinCommand;
import scha.efer.technique.event.impl.juggernaut.command.JuggernautJoinCommand;
import scha.efer.technique.event.impl.lms.command.LMSJoinCommand;
import scha.efer.technique.event.impl.parkour.command.ParkourJoinCommand;
import scha.efer.technique.event.impl.skywars.command.SkyWarsJoinCommand;
import scha.efer.technique.event.impl.spleef.command.SpleefJoinCommand;
import scha.efer.technique.event.impl.sumo.command.SumoJoinCommand;
import scha.efer.technique.event.impl.wipeout.command.WipeoutJoinCommand;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveEventSelectEventMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&5Select an active event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (EventType eventType : EventType.values()) {
            if (eventType.getTitle().equals("FFA")) {
                if (TechniquePlugin.get().getLMSManager().getActiveLMS() != null && TechniquePlugin.get().getLMSManager().getActiveLMS().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.FFA));
                    i++;
                }
            }
            if (eventType.getTitle().equals("Brackets")) {
                if (TechniquePlugin.get().getBracketsManager().getActiveBrackets() != null && TechniquePlugin.get().getBracketsManager().getActiveBrackets().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.BRACKETS));
                    i++;
                }
            }
            if (eventType.getTitle().equals("Sumo")) {
                if (TechniquePlugin.get().getSumoManager().getActiveSumo() != null && TechniquePlugin.get().getSumoManager().getActiveSumo().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.SUMO));
                    i++;
                }
            }
//            if (eventType.getTitle().equals("Juggernaut")) {
//                if (Practice.get().getJuggernautManager().getActiveJuggernaut() != null && Practice.get().getJuggernautManager().getActiveJuggernaut().isWaiting()) {
//                    buttons.put(i, new SelectEventButton(EventType.JUGGERNAUT));
//                    i++;
//                }
//            }
            if (eventType.getTitle().equals("Parkour")) {
                if (TechniquePlugin.get().getParkourManager().getActiveParkour() != null && TechniquePlugin.get().getParkourManager().getActiveParkour().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.PARKOUR));
                    i++;
                }
            }
//            if (eventType.getTitle().equals("Wipeout")) {
//                if (Practice.get().getWipeoutManager().getActiveWipeout() != null && Practice.get().getWipeoutManager().getActiveWipeout().isWaiting()) {
//                    buttons.put(i, new SelectEventButton(EventType.WIPEOUT));
//                    i++;
//                }
//            }
			if (eventType.getTitle().equals("SkyWars")) {
				if (TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars() != null && TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.SKYWARS));
					i++;
				}
			}
            if (eventType.getTitle().equals("Spleef")) {
                if (TechniquePlugin.get().getSpleefManager().getActiveSpleef() != null && TechniquePlugin.get().getSpleefManager().getActiveSpleef().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.SPLEEF));
                    i++;
                }
            }
//            if (eventType.getTitle().equals("Infected")) {
//                if (Practice.get().getInfectedManager().getActiveInfected() != null && Practice.get().getInfectedManager().getActiveInfected().isWaiting()) {
//                    buttons.put(i, new SelectEventButton(EventType.INFECTED));
//                    i++;
//                }
//            }
        }
        return buttons;
    }

    @AllArgsConstructor
    private class SelectEventButton extends Button {

        private final EventType eventType;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();

            if (eventType.getTitle().equals("Brackets")) {
                lore = TechniquePlugin.get().getBracketsManager().getActiveBrackets().getLore();
            } else if (eventType.getTitle().equals("Sumo")) {
                lore = TechniquePlugin.get().getSumoManager().getActiveSumo().getLore();
            } else if (eventType.getTitle().equals("FFA")) {
                lore = TechniquePlugin.get().getLMSManager().getActiveLMS().getLore();
            } else if (eventType.getTitle().equals("Juggernaut")) {
                lore = TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().getLore();
            } else if (eventType.getTitle().equals("Parkour")) {
                lore = TechniquePlugin.get().getParkourManager().getActiveParkour().getLore();
            } else if (eventType.getTitle().equals("Wipeout")) {
                lore = TechniquePlugin.get().getWipeoutManager().getActiveWipeout().getLore();
            } else if (eventType.getTitle().equals("SkyWars")) {
                lore = TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars().getLore();
            } else if (eventType.getTitle().equals("Spleef")) {
                lore = TechniquePlugin.get().getSpleefManager().getActiveSpleef().getLore();
            } else if (eventType.getTitle().equals("Infected")) {
                lore = TechniquePlugin.get().getInfectedManager().getActiveInfected().getLore();
            }

            lore.add("&f(Left-Click to join)");
            lore.add(CC.MENU_BAR);


            return new ItemBuilder(eventType.getMaterial())
                    .name("&5" + eventType.getTitle() + "&f Event")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
            switch (eventType.getTitle()) {
                case "Brackets":
                    BracketsJoinCommand.execute(player);
                    break;
                case "Sumo":
                    SumoJoinCommand.execute(player);
                    break;
                case "FFA":
                    LMSJoinCommand.execute(player);
                    break;
                case "Juggernaut":
                    JuggernautJoinCommand.execute(player);
                    break;
                case "Parkour":
                    ParkourJoinCommand.execute(player);
                    break;
                case "Wipeout":
                    WipeoutJoinCommand.execute(player);
                    break;
                case "SkyWars":
                    SkyWarsJoinCommand.execute(player);
                    break;
                case "Spleef":
                    SpleefJoinCommand.execute(player);
                    break;
                case "Infected":
                    InfectedJoinCommand.execute(player);
                    break;
            }
        }

    }

}
