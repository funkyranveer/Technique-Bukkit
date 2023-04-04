package scha.efer.technique.party.menu;

import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.impl.HCFMatch;
import scha.efer.technique.match.impl.KoTHMatch;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.party.Party;
import scha.efer.technique.party.PartyEvent;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PartyEventSelectEventMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&5Select a party event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(3, new SelectEventButton(PartyEvent.SPLIT));
        buttons.put(5, new SelectEventButton(PartyEvent.FFA));
        buttons.put(6, new SelectEventButton(PartyEvent.HCF));
        buttons.put(4, new SelectEventButton(PartyEvent.KOTH));
        return buttons;
    }

    @AllArgsConstructor
    private class SelectEventButton extends Button {

        private final PartyEvent partyEvent;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(partyEvent.getMaterial())
                    .name("&5" + partyEvent.getName())
                    .lore("&f" + partyEvent.getLore())
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }
            if (partyEvent == PartyEvent.FFA || partyEvent == PartyEvent.SPLIT) {
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                new PartyEventSelectKitMenu(partyEvent).openMenu(player);
            } else {
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

                player.closeInventory();

                if (profile.getParty() == null) {
                    player.sendMessage(CC.RED + "You are not in a party.");
                    return;
                }

                if (profile.getParty().getTeamPlayers().size() <= 1) {
                    player.sendMessage(CC.RED + "You do not have enough players in your party to start a party event.");
                    return;
                }

                Party party = profile.getParty();
                Arena arena;

                if (partyEvent.equals(PartyEvent.HCF)) arena = Arena.getRandom(Kit.getByName("NoDebuff"));
                else arena = Arena.getRandom(Kit.getByName("KoTH"));

                if (arena == null) {
                    player.sendMessage(CC.RED + "There are no available arenas.");
                    return;
                }

                arena.setActive(true);

                Match match;

                Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
                Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));

                List<Player> players = new ArrayList<>();
                players.addAll(party.getPlayers());
                Collections.shuffle(players);

                // Create match
                if (partyEvent.equals(PartyEvent.HCF)) match = new HCFMatch(teamA, teamB, arena);
                else match = new KoTHMatch(teamA, teamB, arena);

                for (Player otherPlayer : players) {
                    if (teamA.getLeader().getUuid().equals(otherPlayer.getUniqueId()) ||
                            teamB.getLeader().getUuid().equals(otherPlayer.getUniqueId())) {
                        continue;
                    }

                    if (teamA.getTeamPlayers().size() > teamB.getTeamPlayers().size()) {
                        teamB.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                    } else {
                        teamA.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                    }
                }

                // Start match
                match.start();
            }
        }

    }

}
