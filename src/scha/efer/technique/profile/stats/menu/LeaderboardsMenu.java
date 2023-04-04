package scha.efer.technique.profile.stats.menu;

import scha.efer.technique.clan.Clan;
import scha.efer.technique.kit.KitLeaderboards;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardsMenu extends Menu {

    public String getTitle(Player player) {
        return "&5Leaderboards";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(3, new KitLeaderboardsButton("Ranked"));
        buttons.put(5, new KitLeaderboardsButton("Premium"));

        return buttons;
    }

    @AllArgsConstructor
    private class KitLeaderboardsButton extends Button {

        private final String type;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            if (type.equalsIgnoreCase("ranked")) {
                lore.add("&8&m-----------------------------------------");
                lore.add("&fClick here to go to the &5" + type + " &fleaderboards");
                lore.add("&8&m-----------------------------------------");

                return new ItemBuilder(Material.GOLD_SWORD)
                        .name("&5Ranked")
                        .lore(lore)
                        .build();
            } else {
                lore.add("&8&m--------------------------");
                int added = 1;
                for (KitLeaderboards kitLeaderboards : Clan.getClanEloLeaderboards()) {
                    lore.add(" &5#" + added + " &8- &f" + kitLeaderboards.getName() + "&8 - &5" + kitLeaderboards.getElo());
                    added++;
                }
                lore.add("&8&m--------------------------");

                return new ItemBuilder(Material.DIAMOND_SWORD)
                        .name("&5Clan")
                        .lore(lore)
                        .build();
            }
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new RankedLeaderboardsMenu().openMenu(player);
        }

    }
}
