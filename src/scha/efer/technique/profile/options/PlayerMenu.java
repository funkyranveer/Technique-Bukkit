package scha.efer.technique.profile.options;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.hotbar.Hotbar;
import scha.efer.technique.profile.hotbar.HotbarLayout;
import scha.efer.technique.profile.stats.menu.LeaderboardsMenu;
import scha.efer.technique.profile.stats.menu.ProfileMenu;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PlayerMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&5Options";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(1, new LeaderBoardsButton());
        buttons.put(4, new StatisticsButton());
        buttons.put(7, new SettingsButton());
        buttons.put(19, new PartyButton());
        buttons.put(22, new ShowcaseButton());
        buttons.put(25, new LeaderboardButton());

        return buttons;
    }

    @AllArgsConstructor
    private class LeaderBoardsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("&aClick here to go to the leaderboards");
            return new ItemBuilder(Material.ITEM_FRAME)
                    .name("&5Leaderboards")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new LeaderboardsMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private class StatisticsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("&aClick here to go to your statistics");
            return new ItemBuilder(Material.PAPER)
                    .name("&5Statistics")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new ProfileMenu(player).openMenu(player);
        }

    }

    @AllArgsConstructor
    private class SettingsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("&aClick here to go change your preferences");
            return new ItemBuilder(Material.ANVIL)
                    .name("&5Settings")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new OptionsMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private class PartyButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("&aClick here to create a party");
            return new ItemBuilder(Material.NAME_TAG)
                    .name("&5Create Party")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            player.chat("/party create");
        }

    }

    @AllArgsConstructor
    private class ShowcaseButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("&aClick here to teleport to the pack showcase");
            return new ItemBuilder(Material.PAINTING)
                    .name("&5Pack Showcase")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            TechniquePlugin.get().getEssentials().teleportToPackShowcase(player);
            player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.ROOM, Profile.getByUuid(player)));
            player.updateInventory();
        }

    }

    @AllArgsConstructor
    private class LeaderboardButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("&aClick here to teleport to the leaderboards room");
            return new ItemBuilder(Material.LEASH)
                    .name("&5Leaderboards Room")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            player.teleport(new Location(Bukkit.getWorld("world"), 50.5, 39.0, -41.5, 359.97454833984375F, -1.4053070545196533F));
            player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.ROOM, Profile.getByUuid(player)));
            player.updateInventory();
        }

    }

}
