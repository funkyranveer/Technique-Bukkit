package scha.efer.technique.party.menu;

import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.party.Party;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import scha.efer.technique.util.external.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherPartiesMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&5Other Parties";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Party.getParties().forEach(party -> buttons.put(buttons.size(), new PartyDisplayButton(party)));
        return buttons;
    }

    @AllArgsConstructor
    public static class PartyDisplayButton extends Button {

        private final Party party;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            int added = 0;

            for (TeamPlayer teamPlayer : party.getTeamPlayers()) {
                if (added >= 10) {
                    break;
                }
                if (teamPlayer.getPlayer() != null) {
                    lore.add(CC.GRAY + " * " + CC.RESET + teamPlayer.getPlayer().getName());
                    added++;
                }
            }

            if (party.getTeamPlayers().size() != added) {
                lore.add(CC.GRAY + " and " + (party.getTeamPlayers().size() - added) + " others...");
            }

            return new ItemBuilder(Material.SKULL_ITEM)
                    .name("&5Party of " + party.getLeader().getPlayer().getName())
                    .amount(party.getTeamPlayers().size())
                    .durability(3)
                    .lore(lore)
                    .build();
        }


        @Override
        public void clicked(Player player, ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

            Profile senderProfile = Profile.getByUuid(player.getUniqueId());
            Profile receiverProfile = Profile.getByUuid(party.getLeader().getPlayer().getUniqueId());

            if (!(player.getUniqueId().equals(senderProfile.getParty().getLeader().getPlayer().getUniqueId()))) {
                player.sendMessage(CC.RED + "You can only duel parties as a leader.");
                return;
            }

            if (senderProfile.isBusy(player)) {
                player.sendMessage(CC.RED + "You cannot duel right now.");
                return;
            }

            if (receiverProfile.isBusy(receiverProfile.getParty().getLeader().getPlayer())) {
                player.sendMessage(CC.translate(party.getLeader().getPlayer().getDisplayName()) + CC.RED + " is currently busy.");
                return;
            }

            if (!receiverProfile.getOptions().isReceiveDuelRequests()) {
                player.sendMessage(CC.RED + "That player is not accepting duel requests at the moment.");
                return;
            }

            if (!senderProfile.canSendDuelRequest(player)) {
                player.sendMessage(CC.RED + "You have already sent that player a duel request.");
                return;
            }

            if (senderProfile.getParty() != null && receiverProfile.getParty() == null) {
                player.sendMessage(CC.RED + "That player is not in a party.");
                return;
            }

            if (player.getUniqueId().equals(receiverProfile.getParty().getLeader().getPlayer().getUniqueId())) {
                player.sendMessage(CC.RED + "You cannot duel yourself.");
                return;
            }

            new OtherPartiesSelectEventMenu(player, party.getLeader().getPlayer(), party).openMenu(player);
        }

    }
}
