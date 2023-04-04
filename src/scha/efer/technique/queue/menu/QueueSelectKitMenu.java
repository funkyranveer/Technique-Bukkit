package scha.efer.technique.queue.menu;

import gg.smok.core.plugin.SmokCore;
import me.activated.core.api.player.PlayerData;
import org.bukkit.enchantments.Enchantment;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.kit.KitLeaderboards;
import scha.efer.technique.match.Match;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.queue.Queue;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class QueueSelectKitMenu extends Menu {

    private final QueueType queueType;

    {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_PURPLE + (queueType.name().toLowerCase().substring(0, 1).toUpperCase() + queueType.name().toLowerCase().substring(1)) + " Queue";
    }

    private int getSlotsUnranked() {
        for (Queue queue : Queue.getQueues()) {
            if (queue.getKit().getName().equalsIgnoreCase("NoDebuff")) {
                return 1;
            } if (queue.getKit().getName().equalsIgnoreCase("Debuff")) {
                return 2;
            }
        }

        return 0;
    }


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (Queue queue : Queue.getQueues()) {
            if (queue.getType() == queueType) {
                if (queue.getType() == QueueType.UNRANKED) {
                    buttons.put(queue.getKit().getUnrankedSlot(), new SelectKitButton(queue));
                } else if (queue.getType() == QueueType.RANKED) {
                    buttons.put(queue.getKit().getRankedSlot(), new SelectKitButton(queue));
                } else {
                    buttons.put(i++, new SelectKitButton(queue));
                }
            }
        }

        return buttons;
    }

    @AllArgsConstructor
    private class SelectKitButton extends Button {

        private final Queue queue;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());
            PlayerData playerData = SmokCore.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());
            Kit kit = Kit.getByName(queue.getKit().getName());
            KitLeaderboards kitLeaderboards;

            lore.add("&fFighting: &5" + Match.getInFights(queue));
            lore.add("&fQueuing: &5" + queue.getPlayers().size());
            lore.add("");
            lore.add("&fPlayed Games: &5" + (profile.getKitData().get(queue.getKit()).getUnrankedWon() + profile.getKitData().get(queue.getKit()).getRankedWon() + profile.getKitData().get(queue.getKit()).getUnrankedLost() + profile.getKitData().get(queue.getKit()).getRankedLost()));
            lore.add("&fTotal Wins: &5" + (profile.getKitData().get(queue.getKit()).getUnrankedWon() + profile.getKitData().get(queue.getKit()).getRankedWon()));
            lore.add("&fTotal Loses: &5" + (profile.getKitData().get(queue.getKit()).getUnrankedLost() + profile.getKitData().get(queue.getKit()).getRankedLost()));
            /*lore.add(" ");
            lore.add("&5Top 3 Players");
            if (kit.getRankedEloLeaderboards().get(0) == null) {
                lore.add(" &3 1) &fNone");
            } else {
                lore.add(" &3 1) &f" + kit.getRankedEloLeaderboards().get(0).getName() + "&7 (" + kit.getRankedEloLeaderboards().get(0).getElo() + ")");
            }
            if (kit.getRankedEloLeaderboards().get(1) == null) {
                lore.add(" &3 2) &fNone");
            } else {
                lore.add(" &3 2) &f" + kit.getRankedEloLeaderboards().get(1).getName() + "&7 (" + kit.getRankedEloLeaderboards().get(1).getElo() + ")");
            }
            if (kit.getRankedEloLeaderboards().get(2) == null) {
                lore.add(" &3 3) &fNone");
            } else {
                lore.add(" &3 3) &f" + kit.getRankedEloLeaderboards().get(2).getName() + "&7 (" + kit.getRankedEloLeaderboards().get(2).getElo() + ")");
            }*/
            lore.add(" ");
            lore.add("&5Click to play &f" + queue.getKit().getName() + "&5.");

            if (queue.getKit().getName().equalsIgnoreCase("Boxing") || queue.getKit().getName().equalsIgnoreCase("Stick-Fight") || queue.getKit().getName().equalsIgnoreCase("SkyWars")) {
                return new ItemBuilder(queue.getKit().getDisplayIcon())
                        .name("&5" + queue.getKit().getName() + " &a&l(POPULAR!)")
                        .lore(lore)
                        .enchantment(Enchantment.THORNS, 1)
                        .amount(Match.getInFights(queue))

                        .build();
            } else if (queue.getKit().getName().equalsIgnoreCase("The-Bridge") || queue.getKit().getName().equalsIgnoreCase("MLG-Rush")) {
                return new ItemBuilder(queue.getKit().getDisplayIcon())
                        .name("&5" + queue.getKit().getName() + " &c&l(BETA!)")
                        .lore(lore)
                        .amount(Match.getInFights(queue))

                        .build();
            }
            else {
                return new ItemBuilder(queue.getKit().getDisplayIcon())
                        .name("&5" + queue.getKit().getName())
                        .lore(lore)
                        .amount(Match.getInFights(queue))

                        .build();
            }
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

            if (queueType == QueueType.UNRANKED) {
                queue.addPlayer(player, 0);
            } else if (queueType == QueueType.RANKED) {
                queue.addPlayer(player, profile.getKitData().get(queue.getKit()).getElo());
            } else if (queueType == QueueType.CLAN) {
                queue.addPlayer(player, 0);
            }
        }

    }
}
