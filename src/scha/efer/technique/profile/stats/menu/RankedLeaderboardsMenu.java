package scha.efer.technique.profile.stats.menu;

import scha.efer.technique.kit.Kit;
import scha.efer.technique.kit.KitLeaderboards;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankedLeaderboardsMenu extends Menu {

    public String getTitle(Player player) {
        return "&5Leaderboards";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlobalLeaderboardsButton());
        for (Kit kit : Kit.getKits()) {
            if (kit.isEnabled()) {
                if (kit.getGameRules().isRanked()) {
                    buttons.put(buttons.size(), new KitLeaderboardsButton(kit));
                }
            }
        }

        return buttons;
    }

    @AllArgsConstructor
    private class KitLeaderboardsButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7&m--------------------------");
            int added = 1;
            for (KitLeaderboards kitLeaderboards : kit.getRankedEloLeaderboards()) {
                if (added == 1 || added == 2 || added == 3) {
                    lore.add("&5" + added + ") &f" + kitLeaderboards.getName() + "&7: &d" + kitLeaderboards.getElo());
                } else {
                    lore.add("&5" + added + ") &f" + kitLeaderboards.getName() + "&7: &d" + kitLeaderboards.getElo());
                }
                added++;
            }
            lore.add("&7&m--------------------------");

            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&5" + kit.getName() + "&7 ⎜ &fTop 10")
                    .lore(lore)
                    .build();
        }

    }

    @AllArgsConstructor
    private class GlobalLeaderboardsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7&m--------------------------");
            int added = 1;
            for (KitLeaderboards kitLeaderboards : Profile.getGlobalEloLeaderboards()) {
                if (added == 1 || added == 2 || added == 3) {
                    lore.add("&5" + added + ") &f" + kitLeaderboards.getName() + "&7: &d" + kitLeaderboards.getElo());
                } else {
                    lore.add("&5" + added + ") &f" + kitLeaderboards.getName() + "&7: &d" + kitLeaderboards.getElo());
                }
                added++;
            }
            lore.add("&7&m--------------------------");

            return new ItemBuilder(Material.GOLD_INGOT)
                    .name("&5Global &7⎜ &fTop 10")
                    .lore(lore)
                    .build();
        }

    }
}
