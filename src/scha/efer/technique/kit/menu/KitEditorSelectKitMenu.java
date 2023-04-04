package scha.efer.technique.kit.menu;

import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitEditorSelectKitMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&5Select a kit to edit.";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Kit.getKits().forEach(kit -> {
            if (kit.isEnabled()) {
                if (kit.getGameRules().isEditable()) {
                    buttons.put(buttons.size(), new KitDisplayButton(kit));
                }
            }
        });

        return buttons;
    }

    @AllArgsConstructor
    private class KitDisplayButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&d" + kit.getName())
                    .lore("&eClick here to edit &d" + kit.getName() + "&e.")
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.getKitEditor().setSelectedKit(kit);
            profile.getKitEditor().setPreviousState(profile.getState());

            new KitManagementMenu(kit).openMenu(player);
        }

    }
}
