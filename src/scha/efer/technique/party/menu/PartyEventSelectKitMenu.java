package scha.efer.technique.party.menu;

import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.impl.FFAMatch;
import scha.efer.technique.match.impl.SumoTeamMatch;
import scha.efer.technique.match.impl.TeamMatch;
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

@AllArgsConstructor
public class PartyEventSelectKitMenu extends Menu {

    private final PartyEvent partyEvent;

    @Override
    public String getTitle(Player player) {
        return "&5Select a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Kit kit : Kit.getKits()) {
            if (kit.isEnabled() && kit.getGameRules().isPartyffa()) {
                buttons.put(buttons.size(), new SelectKitButton(partyEvent, kit));
            }
        }

        return buttons;
    }

    @AllArgsConstructor
    private class SelectKitButton extends Button {

        private final PartyEvent partyEvent;
        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&5" + kit.getName())
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

            player.closeInventory();

            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }

            if (profile.getParty().getTeamPlayers().size() <= 1) {
                player.sendMessage(CC.RED + "You do not have enough players in your party to start an sumo.");
                return;
            }

            Party party = profile.getParty();
            Arena arena = Arena.getRandom(kit);

            if (arena == null) {
                player.sendMessage(CC.RED + "There are no available arenas.");
                return;
            }

            arena.setActive(true);

            Match match;

            if (partyEvent == PartyEvent.FFA) {
                Team team = new Team(new TeamPlayer(party.getLeader().getPlayer()));

                List<Player> players = new ArrayList<>();
                players.addAll(party.getPlayers());

                match = new FFAMatch(team, kit, arena);

                for (Player otherPlayer : players) {
                    if (team.getLeader().getUuid().equals(otherPlayer.getUniqueId())) {
                        continue;
                    }

                    team.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                }
            } else {
                Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
                Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));

                List<Player> players = new ArrayList<>();
                players.addAll(party.getPlayers());
                Collections.shuffle(players);

                // Create match
                if(kit.getGameRules().isSumo()) {
                    match = new SumoTeamMatch(teamA, teamB, kit, arena);
                } else {
                    match = new TeamMatch(teamA, teamB, kit, arena);
                }

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
            }

            // Start match
            match.start();
        }

    }

}
