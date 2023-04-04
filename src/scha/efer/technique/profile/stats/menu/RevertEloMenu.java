package scha.efer.technique.profile.stats.menu;

import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.meta.ProfileMatchHistory;
import scha.efer.technique.util.external.CC;
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

@AllArgsConstructor
public class RevertEloMenu extends Menu {

    private final ProfileMatchHistory profileMatchHistory;

    @Override
    public String getTitle(Player player) {
        return "&5Revert Elo";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new RevertEloButton());

        return buttons;
    }

    @AllArgsConstructor
    private class RevertEloButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m-------------------------------");
            lore.add("&fAre you sure you want to make the following changes:");
            lore.add("&5" + profileMatchHistory.getFighter().getTeamPlayer().getUsername() + ": &f-" + profileMatchHistory.getEloChangeWinner());
            lore.add("&5" + profileMatchHistory.getOpponent().getTeamPlayer().getUsername() + ": &f+" + profileMatchHistory.getEloChangeLoser());
            lore.add("&8&m-------------------------------");
            lore.add("&fLeft-Click to confirm");
            lore.add("&fRight-Click to cancel");
            lore.add("&8&m-------------------------------");
            return new ItemBuilder(Material.PAPER)
                    .name("&5Revert Elo")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            if (clickType.isLeftClick()) {
                Kit kit = Kit.getByName(profileMatchHistory.getKit());
                Profile winner = Profile.getByUuid(profileMatchHistory.getFighter().getTeamPlayer().getUuid());
                Profile loser = Profile.getByUuid(profileMatchHistory.getOpponent().getTeamPlayer().getUuid());
                winner.getKitData().get(kit).setElo(winner.getKitData().get(kit).getElo() - profileMatchHistory.getEloChangeWinner());
                loser.getKitData().get(kit).setElo(loser.getKitData().get(kit).getElo() + profileMatchHistory.getEloChangeLoser());
                winner.getMatchHistory().remove(profileMatchHistory);
                loser.getMatchHistory().remove(profileMatchHistory);
                winner.save();
                loser.save();
                player.sendMessage(CC.translate("&5You made the following changes:"));
                player.sendMessage(CC.translate("&5" + profileMatchHistory.getFighter().getTeamPlayer().getUsername() + ": &f-" + profileMatchHistory.getEloChangeWinner()));
                player.sendMessage(CC.translate("&5" + profileMatchHistory.getOpponent().getTeamPlayer().getUsername() + ": &f+" + profileMatchHistory.getEloChangeLoser()));

            }
        }

    }

}
