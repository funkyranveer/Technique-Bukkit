package scha.efer.technique.match.menu;

import scha.efer.technique.match.MatchSnapshot;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.util.InventoryUtil;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.PotionUtil;
import scha.efer.technique.util.external.TimeUtil;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import scha.efer.technique.util.external.menu.button.DisplayButton;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@AllArgsConstructor
public class MatchDetailsMenu extends Menu {

    private final MatchSnapshot snapshot;
    private final MatchSnapshot opponent;

    @Override
    public String getTitle(Player player) {
        return "&eInventory of &d" + snapshot.getTeamPlayer().getPlayer().getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        ItemStack[] fixedContents = InventoryUtil.fixInventoryOrder(snapshot.getContents());

        for (int i = 0; i < fixedContents.length; i++) {
            ItemStack itemStack = fixedContents[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(i, new DisplayButton(itemStack, true));
            }
        }

        for (int i = 0; i < snapshot.getArmor().length; i++) {
            ItemStack itemStack = snapshot.getArmor()[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(39 - i, new DisplayButton(itemStack, true));
            }
        }

        int pos = 45;

        buttons.put(pos++, new HealthButton(snapshot.getHealth()));
        buttons.put(pos++, new HungerButton(snapshot.getHunger()));
        buttons.put(pos++, new EffectsButton(snapshot.getEffects()));

        if (snapshot.shouldDisplayRemainingPotions()) {
            buttons.put(pos++, new PotionsButton(snapshot.getTeamPlayer().getUsername(), snapshot.getRemainingPotions()));
        }

        buttons.put(pos, new StatisticsButton(snapshot.getTeamPlayer()));

        if (this.snapshot.getSwitchTo() != null || this.opponent != null) {
            buttons.put(53, new SwitchInventoryButton(this.snapshot.getSwitchTo()));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class SwitchInventoryButton extends Button {

        private final TeamPlayer switchTo;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (opponent != null) {
                return new ItemBuilder(Material.LEVER)
                        .name("&dOpponent's Inventory")
                        .lore("&eSwitch to &d" + opponent.getTeamPlayer().getUsername() + "&e's inventory")
                        .build();
            }

            return new ItemBuilder(Material.LEVER)
                    .name("&dOpponent's Inventory")
                    .lore("&eSwitch to &d" + switchTo.getUsername() + "&e's inventory")
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (opponent != null) {
                new MatchDetailsMenu(opponent, snapshot).openMenu(player);
            } else {
                MatchSnapshot cachedInventory;

                try {
                    cachedInventory = MatchSnapshot.getByUuid(UUID.fromString(switchTo.getUuid().toString()));
                } catch (Exception e) {
                    cachedInventory = MatchSnapshot.getByName(switchTo.getUuid().toString());
                }

                if (cachedInventory == null) {
                    player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
                    return;
                }

                new MatchDetailsMenu(cachedInventory, null).openMenu(player);
            }
        }

    }

    @AllArgsConstructor
    private class HealthButton extends Button {

        private final int health;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.MELON)
                    .name("&eHealth: &d" + health + "/10&4" + StringEscapeUtils.unescapeJava("\u2764"))
                    .amount(health == 0 ? 1 : health)
                    .build();
        }

    }

    @AllArgsConstructor
    private class HungerButton extends Button {

        private final int hunger;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.COOKED_BEEF)
                    .name("&eHunger: &d" + hunger + "/20")
                    .amount(hunger == 0 ? 1 : hunger)
                    .build();
        }

    }

    @AllArgsConstructor
    private class EffectsButton extends Button {

        private final Collection<PotionEffect> effects;

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder builder = new ItemBuilder(Material.POTION).name("&dPotion Effects");

            if (effects.isEmpty()) {
                builder.lore("&fNo potion effects");
            } else {
                List<String> lore = new ArrayList<>();

                effects.forEach(effect -> {
                    String name = PotionUtil.getName(effect.getType()) + " " + (effect.getAmplifier() + 1);
                    String duration = " (" + TimeUtil.millisToTimer((effect.getDuration() / 20) * 1000) + ")";
                    lore.add(CC.GRAY + name + duration);
                });

                builder.lore(lore);
            }

            return builder.build();
        }

    }

    @AllArgsConstructor
    private class PotionsButton extends Button {

        private final String name;
        private final int potions;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.POTION)
                    .durability(16421)
                    .amount(potions == 0 ? 1 : potions)
                    .name("&dPotions")
                    .lore("&e" + name + " had " + potions + " potion" + (potions == 1 ? "" : "s") + " left.")
                    .build();
        }

    }

    @AllArgsConstructor
    private class StatisticsButton extends Button {

        private final TeamPlayer teamPlayer;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .name("&dStatistics")
                    .lore(Arrays.asList(
                            "&eTotal Hits: &d" + teamPlayer.getHits(),
                            "&eLongest Combo: &d" + teamPlayer.getLongestCombo(),
                            "&ePotions Thrown: &d" + teamPlayer.getPotionsThrown(),
                            "&ePotions Missed: &d" + teamPlayer.getPotionsMissed(),
                            "&ePotion Accuracy: &d" + teamPlayer.getPotionAccuracy()
                    ))
                    .build();
        }

    }

}
