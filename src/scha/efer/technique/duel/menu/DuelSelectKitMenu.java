package scha.efer.technique.duel.menu;

import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DuelSelectKitMenu extends Menu {

    String type;

    public DuelSelectKitMenu(String type) {
        this.type = type;
    }

    @Override
    public String getTitle(Player player) {
        return "&5Select a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        boolean party = Profile.getByUuid(player.getUniqueId()).getParty() != null;

        for (Kit kit : Kit.getKits()) {
            if (kit.isEnabled()) {
                if (!(kit.getGameRules().isTimed() && party))
                    buttons.put(buttons.size(), new SelectKitButton(kit));
            }
        }

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        if (!isClosedByMenu()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setDuelProcedure(null);
        }
    }

    @AllArgsConstructor
    private class SelectKitButton extends Button {

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
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (type.equalsIgnoreCase("normal")) {

                if (profile.getDuelProcedure() == null) {
                    player.sendMessage(CC.RED + "Could not find duel procedure.");
                    return;
                }

                Arena arena = Arena.getRandom(kit);
                // Update duel procedure
                profile.getDuelProcedure().setKit(kit);
                profile.getDuelProcedure().setArena(arena);

                // Set closed by menu
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

                // Force close inventory
                player.closeInventory();

                if (player.hasPermission("technique.selectarena")) {
                    new DuelSelectArenaMenu("normal").openMenu(player);
                } else {
                    profile.getDuelProcedure().send();
                }
            } else if (type.equalsIgnoreCase("rematch")) {
                if (profile.getRematchData() == null) {
                    player.sendMessage(CC.RED + "Could not find rematch data.");
                    return;
                }

                Profile targetProfile = Profile.getByUuid(profile.getRematchData().getTarget());

                Arena arena = Arena.getRandom(kit);
                // Update rematch data
                profile.getRematchData().setKit(kit);
                profile.getRematchData().setArena(arena);
                targetProfile.getRematchData().setKit(kit);
                targetProfile.getRematchData().setArena(arena);

                // Force close inventory
                player.closeInventory();

                if (player.hasPermission("technique.selectarena")) {
                    new DuelSelectArenaMenu("rematch").openMenu(player);
                } else {
                    profile.getRematchData().request();
                }
            }
        }

    }

}
