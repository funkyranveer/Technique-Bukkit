package scha.efer.technique.party.menu;

import scha.efer.technique.party.PartyManage;
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
public class ManagePartyMember extends Menu {

    Player target;

    @Override
    public String getTitle(Player player) {
        return "&5Select an action for &5" + target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(3, new SelectManageButton(PartyManage.LEADER));
        buttons.put(5, new SelectManageButton(PartyManage.KICK));
        return buttons;
    }

    @AllArgsConstructor
    private class SelectManageButton extends Button {

        private final PartyManage partyManage;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(partyManage == PartyManage.LEADER ? Material.GOLD_SWORD : Material.REDSTONE)
                    .name("&5&l" + partyManage.getName())
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }

            if (partyManage == PartyManage.LEADER) {
                profile.getParty().leader(player, target);
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
            } else {
                profile.getParty().leave(target, true);
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
            }
        }

    }

}
