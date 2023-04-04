package scha.efer.technique.queue.menu;

import gg.smok.core.plugin.SmokCore;
import lombok.AllArgsConstructor;
import me.activated.core.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class QueueSelectTypeMenu extends Menu {

    //private final static Button EMPTY_FILLER = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " ");

    {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "&5Select a mode!";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new SelectUnranked());
        buttons.put(11, new SelectRanked());
        buttons.put(12, new SelectClan());
        buttons.put(15, new SeasonInformation());

        buttons.put(22, new CloseMenu());

        //22 for leave

        return buttons;
    }

    @AllArgsConstructor
    private class SelectUnranked extends Button {

        public int getInQueues() {
            int inQueues = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile != null) {
                    if (profile.isInQueue()) {
                        inQueues++;
                    }
                }
            }

            return inQueues;
        }

        public int getInFights() {
            int inFights = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile != null) {
                    if (profile.isInFight() || profile.isInEvent()) {
                        inFights++;
                    }
                }
            }

            return inFights;
        }


        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();

            lore.add(" ");
            lore.add("&7This mode will simulate the");
            lore.add("&7ranked matchmaking by");
            lore.add("&7using skill-based without");
            lore.add("&7taking into account or");
            lore.add("&7affecting ELO.");
            lore.add(" ");
            lore.add("&fIn Fights: &5" + getInFights());
            lore.add("&fIn Queues: &5" + getInQueues());
            lore.add(" ");
            lore.add("&aClick to play this mode!");

            return new ItemBuilder(Material.WOOD_SWORD)
                    .name("&5Unranked Queue")
                    .lore(lore)
                    .amount(1)

                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile == null) {
                return;
            }

            if (player.hasMetadata("frozen")) {
                player.sendMessage(CC.RED + "You cannot queue while frozen.");
                return;
            }

            if (profile.isBusy(player)) {
                player.sendMessage(CC.RED + "You cannot queue right now.");
                return;
            }

            player.closeInventory();

            new QueueSelectKitMenu(QueueType.UNRANKED).openMenu(player);
            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5F, 5F);
        }

    }

    @AllArgsConstructor
    private class SelectRanked extends Button {

        public int getInQueues() {
            int inQueues = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile != null) {
                    if (profile.isInQueue()) {
                        inQueues++;
                    }
                }
            }

            return inQueues;
        }

        public int getInFights() {
            int inFights = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile != null) {
                    if (profile.isInFight() || profile.isInEvent()) {
                        inFights++;
                    }
                }
            }

            return inFights;
        }


        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();

            lore.add(" ");
            lore.add("&7The results of ranked");
            lore.add("&7matches are used to assign");
            lore.add("&7the summoner to a tier and");
            lore.add("&7division in the season.");
            lore.add(" ");
            lore.add("&fIn Fights: &5" + getInFights());
            lore.add("&fIn Queues: &5" + getInQueues());
            lore.add(" ");
            lore.add("&aClick to play this mode!");

            return new ItemBuilder(Material.STONE_SWORD)
                    .name("&5Ranked Queue")
                    .lore(lore)
                    .amount(1)

                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile == null) {
                return;
            }

            if (player.hasMetadata("frozen")) {
                player.sendMessage(CC.RED + "You cannot queue while frozen.");
                return;
            }

            if (profile.isBusy(player)) {
                player.sendMessage(CC.RED + "You cannot queue right now.");
                return;
            }

            player.closeInventory();

            new QueueSelectKitMenu(QueueType.RANKED).openMenu(player);
            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5F, 5F);
        }

    }

    @AllArgsConstructor
    private class SelectClan extends Button {

        public int getInQueues() {
            int inQueues = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile != null) {
                    if (profile.isInQueue()) {
                        inQueues++;
                    }
                }
            }

            return inQueues;
        }

        public int getInFights() {
            int inFights = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile != null) {
                    if (profile.isInFight() || profile.isInEvent()) {
                        inFights++;
                    }
                }
            }

            return inFights;
        }


        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());
            PlayerData playerData = SmokCore.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());
            Clan clan = Clan.getByMember(player.getUniqueId());
            if (clan != null) {
                lore.add(" ");
                lore.add("&7This mode is a strategic battle");
                lore.add("&7between two clans. Battle with");
                lore.add("&7your clan for glory and ranking");
                lore.add("&7against other clans!");
                lore.add(" ");
                lore.add("&fIn Fights: &5" + getInFights());
                lore.add("&fIn Queues: &5" + getInQueues());
                lore.add(" ");
                lore.add("&aClick to play this mode!");
            } else {
                lore.add(" ");
                lore.add("&7This mode is a strategic battle");
                lore.add("&7between two clans. Battle with");
                lore.add("&7your clan for glory and ranking");
                lore.add("&7against other clans!");
                lore.add(" ");
                lore.add("&fIn Fights: &5" + getInFights());
                lore.add("&fIn Queues: &5" + getInQueues());
                lore.add(" ");
                lore.add("&cYou can't play this gamemode.");
                lore.add("&cCreate a clan via /clan create <name>.");
            }

            return new ItemBuilder(Material.GOLD_SWORD)
                    .name("&5Clan Queue")
                    .lore(lore)
                    .amount(1)

                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile == null) {
                return;
            }

            if (player.hasMetadata("frozen")) {
                player.sendMessage(CC.RED + "You cannot queue while frozen.");
                return;
            }

            if (profile.isBusy(player)) {
                player.sendMessage(CC.RED + "You cannot queue right now.");
                return;
            }

            player.closeInventory();

            if (!profile.isBusy(player)) {
                Clan clan = Clan.getByMember(player.getUniqueId());
                if (clan != null) {
                    new QueueSelectKitMenu(QueueType.CLAN).openMenu(player);
                    player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5F, 5F);
                } else {
                    player.sendMessage(CC.RED + "You must have a clan to join clan queue.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5F, 5F);
                }
            }
        }

    }

    @AllArgsConstructor
    private class SeasonInformation extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();

            lore.add(" ");
            lore.add("&7Compete against others in");
            lore.add("&7our various game-modes and");
            lore.add("&7make your way towards the top");
            lore.add("&7of the leaderboards to earn prizes.");
            lore.add(" ");
            lore.add("&aClick to see the prizes.");

            return new ItemBuilder(Material.NETHER_STAR)
                    .name("&5Current Season")
                    .lore(lore)
                    .amount(1)

                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile == null) {
                return;
            }

            player.closeInventory();

            player.sendMessage(CC.translate(" "));
            player.sendMessage(CC.translate("&5&lSeason 3 Prizes"));
            player.sendMessage(CC.translate("&a  1) &fUSD$30 PayPal"));
            player.sendMessage(CC.translate("&6  2) &fUSD$10 PayPal"));
            player.sendMessage(CC.translate("&c  3) &f$10 Store Credit"));
            player.sendMessage(CC.translate(" "));
            player.sendMessage(CC.translate("&fSeason 3 has started at &5November 22th 2021."));
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 5F, 5F);
        }

    }

    @AllArgsConstructor
    private class CloseMenu extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();

            lore.add(" ");
            lore.add("&cClick to close the menu.");

            return new ItemBuilder(Material.REDSTONE)
                    .name("&cClose")
                    .lore(lore)
                    .amount(1)

                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile == null) {
                return;
            }

            player.closeInventory();
        }

    }
}
