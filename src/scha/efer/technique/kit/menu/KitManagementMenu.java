package scha.efer.technique.kit.menu;

import scha.efer.technique.kit.Kit;
import scha.efer.technique.kit.KitLoadout;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import scha.efer.technique.util.external.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KitManagementMenu extends Menu {

    private static final Button PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " ");

    private final Kit kit;

    public KitManagementMenu(Kit kit) {
        this.kit = kit;

        setPlaceholder(true);
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return "&5Managing &f(" + kit.getName() + ")";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        KitLoadout[] kitLoadouts = profile.getKitData().get(kit).getLoadouts();

        if (kitLoadouts == null) {
            return buttons;
        }

        int startPos = -1;

        for (int i = 0; i < 4; i++) {
            startPos += 2;

            KitLoadout kitLoadout = kitLoadouts[i];
            buttons.put(startPos, kitLoadout == null ? new CreateKitButton(i) : new KitDisplayButton(kitLoadout));
            buttons.put(startPos + 18, new LoadKitButton(i));
            buttons.put(startPos + 27, kitLoadout == null ? PLACEHOLDER : new RenameKitButton(kitLoadout));
            buttons.put(startPos + 36, kitLoadout == null ? PLACEHOLDER : new DeleteKitButton(kitLoadout));
        }

        buttons.put(36, new BackButton(new KitEditorSelectKitMenu()));

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        if (!isClosedByMenu()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setState(profile.getKitEditor().getPreviousState());
            profile.getKitEditor().setSelectedKit(null);
        }
    }

    @AllArgsConstructor
    private class DeleteKitButton extends Button {

        private final KitLoadout kitLoadout;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .name("&c&lDelete")
                    .durability(14)
                    .lore(Arrays.asList(
                            "",
                            "&7Click to delete this kit.",
                            "&7You will &c&lNOT &cbe able to",
                            "&7recover this kit loadout."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.getKitData().get(kit).deleteKit(kitLoadout);

            new KitManagementMenu(profile.getKitEditor().getSelectedKit()).openMenu(player);
        }

    }

    @AllArgsConstructor
    private class CreateKitButton extends Button {

        private final int index;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.IRON_SWORD)
                    .name("&a&lCreate Kit")
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            Kit kit = profile.getKitEditor().getSelectedKit();

            // TODO: this shouldn't be null but sometimes it is?
            if (kit == null) {
                player.closeInventory();
                return;
            }

            KitLoadout kitLoadout = new KitLoadout("Kit " + (index + 1));

            if (kit.getKitLoadout() != null) {
                if (kit.getKitLoadout().getArmor() != null) {
                    kitLoadout.setArmor(kit.getKitLoadout().getArmor());
                }

                if (kit.getKitLoadout().getContents() != null) {
                    kitLoadout.setContents(kit.getKitLoadout().getContents());
                }
            }

            profile.getKitData().get(kit).replaceKit(index, kitLoadout);
            profile.getKitEditor().setSelectedKitLoadout(kitLoadout);
            new KitEditorMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private class RenameKitButton extends Button {

        private final KitLoadout kitLoadout;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SIGN)
                    .name("&5&lRename")
                    .lore(Arrays.asList(
                            "",
                            "&5Click to rename this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.getKitEditor().setActive(true);
            profile.getKitEditor().setRename(true);
            profile.getKitEditor().setSelectedKitLoadout(kitLoadout);

            player.closeInventory();
            player.sendMessage(CC.RED + "Renaming " + CC.RED + kitLoadout.getCustomName() + CC.RED + "," +
                    CC.GREEN + "Enter the new name now.");
        }

    }

    @AllArgsConstructor
    private class LoadKitButton extends Button {

        private final int index;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                    .name("&a&lLoad/Edit")
                    .lore(Arrays.asList(
                            "",
                            "&fClick to edit this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            // TODO: this shouldn't be null but sometimes it is?
            if (profile.getKitEditor().getSelectedKit() == null) {
                player.closeInventory();
                return;
            }

            KitLoadout kit = profile.getKitData().get(profile.getKitEditor().getSelectedKit()).getLoadout(index);

            if (kit == null) {
                kit = new KitLoadout("Kit " + (index + 1));
                kit.setArmor(profile.getKitEditor().getSelectedKit().getKitLoadout().getArmor());
                kit.setContents(profile.getKitEditor().getSelectedKit().getKitLoadout().getContents());
                profile.getKitData().get(profile.getKitEditor().getSelectedKit()).replaceKit(index, kit);
            }

            profile.getKitEditor().setSelectedKitLoadout(kit);

            new KitEditorMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private class KitDisplayButton extends Button {

        private final KitLoadout kitLoadout;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                    .name("&5&l" + kitLoadout.getCustomName())
                    .build();
        }

    }

}
