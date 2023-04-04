package scha.efer.technique.profile.stats.menu;

import scha.efer.technique.kit.Kit;
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

@AllArgsConstructor
public class ELOMenu extends Menu {

    private final Player target;

    @Override
    public String getTitle(Player player) {
        return "&5" + target.getName() + "'s ELO Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlobalStatsButton());
        for (Kit kit : Kit.getKits()) {
            if (kit.isEnabled()) {
                buttons.put(buttons.size(), new KitStatsButton(kit));
            }
        }

        return buttons;
    }

    @AllArgsConstructor
    private class KitStatsButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());
            String elo = kit.getGameRules().isRanked() ? Integer.toString(profile.getKitData().get(kit).getElo()) : "N/A";
            String rwins = kit.getGameRules().isRanked() ? Integer.toString(profile.getKitData().get(kit).getRankedWon()) : "N/A";
            String rlosses = kit.getGameRules().isRanked() ? Integer.toString(profile.getKitData().get(kit).getRankedLost()) : "N/A";
            String uwins = Integer.toString(profile.getKitData().get(kit).getUnrankedWon());
            String ulosses = Integer.toString(profile.getKitData().get(kit).getUnrankedLost());

            lore.add("&8&m--------------------------");
            lore.add(" &5Ranked:");
            lore.add("  &5ELO: &r" + elo);
            lore.add("  &5Wins: &r" + rwins);
            lore.add("  &5Losses: &r" + rlosses);
            lore.add("");
            lore.add(" &5Unranked:");
            lore.add("  &5Wins: &r" + uwins);
            lore.add("  &5Losses: &r" + ulosses);
            lore.add("&8&m--------------------------");

            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&5" + kit.getName())
                    .lore(lore)
                    .build();
        }

    }

    @AllArgsConstructor
    private class GlobalStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());

            lore.add("&8&m--------------------------");
            lore.add(" &5Global:");
            lore.add("  &5ELO: &r" + profile.getGlobalElo());
            lore.add("  &5League: &r" + profile.getEloLeague());
            lore.add("&8&m--------------------------");

            return new ItemBuilder(Material.GOLD_INGOT)
                    .name("&5Global")
                    .lore(lore)
                    .build();
        }

    }

}
