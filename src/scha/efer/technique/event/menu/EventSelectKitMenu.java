package scha.efer.technique.event.menu;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.brackets.Brackets;
import scha.efer.technique.event.impl.lms.LMS;
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

@AllArgsConstructor
public class EventSelectKitMenu extends Menu {

    private final String event;

    @Override
    public String getTitle(Player player) {
        return "&5Select a kit to host " + event;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (Kit kit : Kit.getKits()) {
            if (kit.isEnabled() && !kit.getGameRules().isNoitems() && !kit.getGameRules().isLavakill() && !kit.getGameRules().isWaterkill() && !kit.getGameRules().isSpleef() && !kit.getGameRules().isBuild() && !kit.getGameRules().isSumo()) {
                buttons.put(buttons.size(), new SelectKitButton(event, kit));
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    private class SelectKitButton extends Button {

        private final String event;
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
            if (event.equals("Brackets")) {
                if (TechniquePlugin.get().getBracketsManager().getActiveBrackets() != null) {
                    player.sendMessage(CC.RED + "There is already an active Brackets Event.");
                    return;
                }

                if (!TechniquePlugin.get().getBracketsManager().getCooldown().hasExpired()) {
                    player.sendMessage(CC.RED + "There is an active cooldown for the Brackets Event.");
                    return;
                }

                TechniquePlugin.get().getBracketsManager().setActiveBrackets(new Brackets(player, kit));

                for (Player other : TechniquePlugin.get().getServer().getOnlinePlayers()) {
                    Profile profile = Profile.getByUuid(other.getUniqueId());

                    if (profile.isInLobby()) {
                        if (!profile.getKitEditor().isActive()) {
                            profile.refreshHotbar();
                        }
                    }
                }
            } else {
                if (TechniquePlugin.get().getLMSManager().getActiveLMS() != null) {
                    player.sendMessage(CC.RED + "There is already an active Brackets Event.");
                    return;
                }

                if (!TechniquePlugin.get().getLMSManager().getCooldown().hasExpired()) {
                    player.sendMessage(CC.RED + "There is an active cooldown for the Brackets Event.");
                    return;
                }

                TechniquePlugin.get().getLMSManager().setActiveLMS(new LMS(player, kit));

                for (Player other : TechniquePlugin.get().getServer().getOnlinePlayers()) {
                    Profile profile = Profile.getByUuid(other.getUniqueId());

                    if (profile.isInLobby()) {
                        if (!profile.getKitEditor().isActive()) {
                            profile.refreshHotbar();
                        }
                    }
                }
            }
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
        }

    }

}
