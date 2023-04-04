package scha.efer.technique.event.menu;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.EventType;
import scha.efer.technique.event.impl.brackets.command.BracketsHostCommand;
import scha.efer.technique.event.impl.brackets.command.BracketsJoinCommand;
import scha.efer.technique.event.impl.infected.command.InfectedHostCommand;
import scha.efer.technique.event.impl.infected.command.InfectedJoinCommand;
import scha.efer.technique.event.impl.juggernaut.command.JuggernautHostCommand;
import scha.efer.technique.event.impl.juggernaut.command.JuggernautJoinCommand;
import scha.efer.technique.event.impl.lms.command.LMSHostCommand;
import scha.efer.technique.event.impl.lms.command.LMSJoinCommand;
import scha.efer.technique.event.impl.parkour.command.ParkourHostCommand;
import scha.efer.technique.event.impl.parkour.command.ParkourJoinCommand;
import scha.efer.technique.event.impl.skywars.command.SkyWarsHostCommand;
import scha.efer.technique.event.impl.skywars.command.SkyWarsJoinCommand;
import scha.efer.technique.event.impl.spleef.command.SpleefHostCommand;
import scha.efer.technique.event.impl.spleef.command.SpleefJoinCommand;
import scha.efer.technique.event.impl.sumo.command.SumoHostCommand;
import scha.efer.technique.event.impl.sumo.command.SumoJoinCommand;
import scha.efer.technique.event.impl.wipeout.command.WipeoutHostCommand;
import scha.efer.technique.event.impl.wipeout.command.WipeoutJoinCommand;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.TimeUtil;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSelectEventMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&5Select an event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(3, new SelectEventButton(EventType.FFA));
        buttons.put(2, new SelectEventButton(EventType.BRACKETS));
        buttons.put(1, new SelectEventButton(EventType.SUMO));
        //buttons.put(3, new SelectEventButton(EventType.JUGGERNAUT));
        buttons.put(7, new SelectEventButton(EventType.PARKOUR));
        //buttons.put(5, new SelectEventButton(EventType.WIPEOUT));
		buttons.put(6, new SelectEventButton(EventType.SKYWARS));
        buttons.put(5, new SelectEventButton(EventType.SPLEEF));
        //buttons.put(7, new SelectEventButton(EventType.INFECTED));
        return buttons;
    }

    private List<String> getDefaultLore(String name) {
        List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        switch (name) {
            case "Sumo":
                if (!TechniquePlugin.get().getSumoManager().getCooldown().hasExpired()) {
                    toReturn.add("&5Cooldown: &f" + TimeUtil.millisToTimer(TechniquePlugin.get().getSumoManager().getCooldown().getRemaining()));
                } else {
                    toReturn.add("&5State: &fCan be hosted");
                }
                toReturn.add("&5Rank to host: &2Basic");
                break;
            case "Brackets":
                if (!TechniquePlugin.get().getBracketsManager().getCooldown().hasExpired()) {
                    toReturn.add("&5Cooldown: &f" + TimeUtil.millisToTimer(TechniquePlugin.get().getBracketsManager().getCooldown().getRemaining()));
                } else {
                    toReturn.add("&5State: &fCan be hosted");
                }
            case "FFA":
                if (!TechniquePlugin.get().getLMSManager().getCooldown().hasExpired()) {
                    toReturn.add("&5Cooldown: &f" + TimeUtil.millisToTimer(TechniquePlugin.get().getLMSManager().getCooldown().getRemaining()));
                } else {
                    toReturn.add("&5State: &fCan be hosted");
                }
                toReturn.add("&5Rank to host: &dRaver");
                break;
            case "SkyWars":
                if (!TechniquePlugin.get().getSkyWarsManager().getCooldown().hasExpired()) {
                    toReturn.add("&5Cooldown: &f" + TimeUtil.millisToTimer(TechniquePlugin.get().getSkyWarsManager().getCooldown().getRemaining()));
                } else {
                    toReturn.add("&5State: &fCan be hosted");
                }
            case "Spleef":
                if (!TechniquePlugin.get().getSpleefManager().getCooldown().hasExpired()) {
                    toReturn.add("&5Cooldown: &f" + TimeUtil.millisToTimer(TechniquePlugin.get().getSpleefManager().getCooldown().getRemaining()));
                } else {
                    toReturn.add("&5State: &fCan be hosted");
                }
                toReturn.add("&5Rank to host: &5Bartender");
                break;
            case "Parkour":
                if (!TechniquePlugin.get().getParkourManager().getCooldown().hasExpired()) {
                    toReturn.add("&5Cooldown: &f" + TimeUtil.millisToTimer(TechniquePlugin.get().getParkourManager().getCooldown().getRemaining()));
                } else {
                    toReturn.add("&5State: &fCan be hosted");
                }
                toReturn.add("&5Rank to host: &3Bouncer");
                break;
        }
        toReturn.add("");

        return toReturn;
    }

    @AllArgsConstructor
    private class SelectEventButton extends Button {

        private final EventType eventType;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();

            switch (eventType.getTitle()) {
                case "Brackets":
                    if (TechniquePlugin.get().getBracketsManager().getActiveBrackets() != null)
                        lore = TechniquePlugin.get().getBracketsManager().getActiveBrackets().getLore();
                    else lore = getDefaultLore("Brackets");
                    break;
                case "Sumo":
                    if (TechniquePlugin.get().getSumoManager().getActiveSumo() != null)
                        lore = TechniquePlugin.get().getSumoManager().getActiveSumo().getLore();
                    else lore = getDefaultLore("Sumo");
                    break;
                case "FFA":
                    if (TechniquePlugin.get().getLMSManager().getActiveLMS() != null)
                        lore = TechniquePlugin.get().getLMSManager().getActiveLMS().getLore();
                    else lore = getDefaultLore("FFA");
                    break;
                case "Juggernaut":
                    if (TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut() != null)
                        lore = TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().getLore();
                    else lore = getDefaultLore("Juggernaut");
                    break;
                case "Parkour":
                    if (TechniquePlugin.get().getParkourManager().getActiveParkour() != null)
                        lore = TechniquePlugin.get().getParkourManager().getActiveParkour().getLore();
                    else lore = getDefaultLore("Parkour");
                    break;
                case "Wipeout":
                    if (TechniquePlugin.get().getWipeoutManager().getActiveWipeout() != null)
                        lore = TechniquePlugin.get().getWipeoutManager().getActiveWipeout().getLore();
                    else lore = getDefaultLore("Wipeout");
                    break;
                case "SkyWars":
                    if (TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars() != null)
                        lore = TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars().getLore();
                    else lore = getDefaultLore("SkyWars");
                    break;
                case "Spleef":
                    if (TechniquePlugin.get().getSpleefManager().getActiveSpleef() != null)
                        lore = TechniquePlugin.get().getSpleefManager().getActiveSpleef().getLore();
                    else lore = getDefaultLore("Spleef");
                    break;
                case "Infected":
                    if (TechniquePlugin.get().getInfectedManager().getActiveInfected() != null)
                        lore = TechniquePlugin.get().getInfectedManager().getActiveInfected().getLore();
                    else lore = getDefaultLore("Infected");
                    break;
            }

            lore.add("&aLeft-Click to join");
            lore.add("&2Right-Click to host");


            return new ItemBuilder(eventType.getMaterial())
                    .name("&5" + eventType.getTitle() + "&f Event")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
            if (clickType.isLeftClick()) {
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
            } else {
                switch (eventType.getTitle()) {
                    case "Brackets":
                        if (player.hasPermission("technique.brackets.host")) BracketsHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                    case "Sumo":
                        if (player.hasPermission("technique.sumo.host")) SumoHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                    case "FFA":
                        if (player.hasPermission("technique.ffa.host")) LMSHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                    case "Juggernaut":
                        if (player.hasPermission("technique.juggernaut.host")) JuggernautHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                    case "Parkour":
                        if (player.hasPermission("technique.parkour.host")) ParkourHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                    case "Wipeout":
                        if (player.hasPermission("technique.wipeout.host")) WipeoutHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                    case "SkyWars":
                        if (player.hasPermission("technique.skywars.host")) SkyWarsHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                    case "Spleef":
                        if (player.hasPermission("technique.spleef.host")) SpleefHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                    case "Infected":
                        if (player.hasPermission("technique.infected.host")) InfectedHostCommand.execute(player);
                        else player.sendMessage(ChatColor.DARK_PURPLE + "No permission.");
                        break;
                }
            }
        }

    }

}
