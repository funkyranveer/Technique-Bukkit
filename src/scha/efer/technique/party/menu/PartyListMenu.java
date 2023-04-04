package scha.efer.technique.party.menu;

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

import java.util.HashMap;
import java.util.Map;

public class PartyListMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&5Party Members";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getParty().getPlayers().forEach(pplayer -> buttons.put(buttons.size(), new PartyDisplayButton(pplayer)));
        return buttons;
    }

    @AllArgsConstructor
    public static class PartyDisplayButton extends Button {
        private final Player pplayer;

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            String lore = profile.getParty().isLeader(player.getUniqueId()) ? "&fClick to manage" : "";

            return new ItemBuilder(Material.SKULL_ITEM)
                    .name("&5" + pplayer.getName())
                    .lore(lore)
                    .durability(3)
                    .build();
        }


        @Override
        public void clicked(Player player, ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

            Profile senderProfile = Profile.getByUuid(player.getUniqueId());
            Profile receiverProfile = Profile.getByUuid(pplayer.getUniqueId());

            if (!(player.getUniqueId().equals(senderProfile.getParty().getLeader().getPlayer().getUniqueId()))) {
                player.sendMessage(CC.RED + "You can only manage players as a leader.");
                return;
            }

            if (pplayer.getUniqueId().equals(receiverProfile.getParty().getLeader().getPlayer().getUniqueId())) {
                player.sendMessage(CC.RED + "You cannot manage yourself.");
                return;
            }


            if (senderProfile.getParty() != null && receiverProfile.getParty() == null) {
                player.sendMessage(CC.RED + "That player is not in a party. (Left just now?)");
                return;
            }

            new ManagePartyMember(pplayer).openMenu(player);

        }

    }
}
