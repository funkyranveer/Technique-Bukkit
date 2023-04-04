package scha.efer.technique.party.menu;

import scha.efer.technique.party.PartyManage;
import scha.efer.technique.party.PartyPrivacy;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ManagePartySettings extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&5Party Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(2, new SelectManageButton(PartyManage.INCREMENTLIMIT));
        buttons.put(4, new SelectManageButton(PartyManage.PUBLIC));
        buttons.put(6, new SelectManageButton(PartyManage.DECREASELIMIT));
        return buttons;
    }

    @AllArgsConstructor
    private class SelectManageButton extends Button {

        private final PartyManage partyManage;

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (partyManage == PartyManage.INCREMENTLIMIT) {
                return new ItemBuilder(Material.INK_SACK)
                        .durability(10)
                        .name("&5" + partyManage.getName())
                        .lore("&fLimit: " + profile.getParty().getLimit())
                        .build();
            } else if (partyManage == PartyManage.PUBLIC) {
                return new ItemBuilder(Material.PAPER)
                        .name("&5" + partyManage.getName())
                        .lore("&fPublic: " + profile.getParty().isPublic())
                        .build();
            } else {
                return new ItemBuilder(Material.INK_SACK)
                        .durability(1)
                        .name("&5" + partyManage.getName())
                        .lore("&fLimit: " + profile.getParty().getLimit())
                        .build();
            }
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
                return;
            }

            if (!player.hasPermission("technique.donator")) {
                player.sendMessage(CC.RED + "You need a Donator Rank for this");
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
                return;
            }

            if (partyManage == PartyManage.INCREMENTLIMIT) {
                if (profile.getParty().getLimit() < 100) {
                    profile.getParty().setLimit(profile.getParty().getLimit() + 1);
                    new ManagePartySettings().openMenu(player);
                }
            } else if (partyManage == PartyManage.PUBLIC) {
                if (!profile.getParty().isPublic()) {
                    profile.getParty().setPublic(true);
                    profile.getParty().setPrivacy(PartyPrivacy.OPEN);
                } else {
                    profile.getParty().setPublic(false);
                    profile.getParty().setPrivacy(PartyPrivacy.CLOSED);
                }
                new ManagePartySettings().openMenu(player);
            } else {
                if (profile.getParty().getLimit() > 1) {
                    profile.getParty().setLimit(profile.getParty().getLimit() - 1);
                    new ManagePartySettings().openMenu(player);
                }
            }
        }

    }

}
