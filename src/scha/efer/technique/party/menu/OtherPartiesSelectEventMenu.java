package scha.efer.technique.party.menu;

import scha.efer.technique.arena.Arena;
import scha.efer.technique.duel.DuelProcedure;
import scha.efer.technique.duel.menu.DuelSelectKitMenu;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.party.OtherPartyEvent;
import scha.efer.technique.party.Party;
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
public class OtherPartiesSelectEventMenu extends Menu {

    Player player;
    Player target;
    Party party;

    @Override
    public String getTitle(Player player) {
        return "&5Select an action for &5" + target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(4, new SelectManageButton(OtherPartyEvent.KIT));
        //buttons.put(5, new SelectManageButton(OtherPartyEvent.HCF));
        return buttons;
    }

    @AllArgsConstructor
    private class SelectManageButton extends Button {

        private final OtherPartyEvent partyManage;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(partyManage == OtherPartyEvent.KIT ? Material.GOLD_SWORD : Material.GOLD_CHESTPLATE)
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

            if (partyManage == OtherPartyEvent.KIT) {
                Profile senderProfile = Profile.getByUuid(player.getUniqueId());

                DuelProcedure procedure = new DuelProcedure(player, party.getLeader().getPlayer(), true);
                senderProfile.setDuelProcedure(procedure);

                new DuelSelectKitMenu("normal").openMenu(player);
            } else {
                Profile senderProfile = Profile.getByUuid(player.getUniqueId());

                DuelProcedure procedure = new DuelProcedure(player, party.getLeader().getPlayer(), true);
                senderProfile.setDuelProcedure(procedure);

                Arena arena = Arena.getRandom(Kit.getByName("NoDebuff"));
                // Update duel procedure
                procedure.setKit(Kit.getByName("HCFDIAMOND"));
                procedure.setArena(arena);

                // Set closed by menu
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

                // Force close inventory
                player.closeInventory();

                procedure.send();
            }
        }

    }

}
