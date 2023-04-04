package scha.efer.technique.profile.stats.menu;

import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.menu.MatchDetailsMenu;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.meta.ProfileMatchHistory;
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
public class MatchHistoryMenu extends Menu {

    private final Player target;
    private final boolean ranked;

    @Override
    public String getTitle(Player player) {
        return "&5" + target.getName() + "'s Match History";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = Profile.getByUuid(target.getUniqueId());
        List<ProfileMatchHistory> profileMatchHistories = profile.getMatchHistory();
        profileMatchHistories.stream()
                .filter(profileMatchHistory -> profileMatchHistory.getMatchType().equalsIgnoreCase("ranked") == ranked)
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .forEach(profileMatchHistory -> buttons.put(buttons.size(), new MatchHistoryButton(profileMatchHistory, (buttons.size() + 1))));

        return buttons;
    }

    @AllArgsConstructor
    private class MatchHistoryButton extends Button {

        private final ProfileMatchHistory profileMatchHistory;
        private final Integer order;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m--------------------------");
            lore.add(" &5Type: &r" + profileMatchHistory.getMatchType().toUpperCase());
            lore.add(" &5Winner: &r" + profileMatchHistory.getFighter().getTeamPlayer().getUsername()
                    + (profileMatchHistory.getMatchType().equalsIgnoreCase("ranked") ? (" (+" + profileMatchHistory.getEloChangeWinner() + " ELO)") : ""));
            lore.add(" &5Loser: &r" + profileMatchHistory.getOpponent().getTeamPlayer().getUsername()
                    + (profileMatchHistory.getMatchType().equalsIgnoreCase("ranked") ? (" (-" + profileMatchHistory.getEloChangeLoser() + " ELO)") : ""));
            lore.add(" &5Date: &r" + profileMatchHistory.getCreatedAt().toLocaleString());
            lore.add(" &5Kit: &r" + profileMatchHistory.getKit());
            if(profileMatchHistory.getMatchType().equalsIgnoreCase("unranked") || profileMatchHistory.getMatchType().equalsIgnoreCase("ranked")) {
                if(profileMatchHistory.getKit().equalsIgnoreCase("sumo")) {
                    lore.add(" &5Winner Points: &r" + profileMatchHistory.getWinnerPoints());
                    lore.add(" &5Loser Points: &r" + profileMatchHistory.getLoserPoints());
                }
            }
            lore.add("&8&m--------------------------");
            lore.add("&fLeft-Click to view the inventories");
            if (player.hasPermission("technique.headstaff") && profileMatchHistory.getMatchType().equalsIgnoreCase("ranked")) {
                lore.add("&fRight-Click to revert this elo change");
                lore.add("&5READ ALL TEXT IN THE NEXT MENU CAREFULLY");
            }
            lore.add("&8&m--------------------------");

            return new ItemBuilder(Kit.getByName(profileMatchHistory.getKit()).getDisplayIcon())
                    .name((profileMatchHistory.isWon() ? "&a" : "&5") + "&lMatch #" + order + (profileMatchHistory.isWon() ? " (Won)" : " (Lost)"))
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            if (player.hasPermission("technique.headstaff") && profileMatchHistory.getMatchType().equalsIgnoreCase("ranked")) {
                if (clickType.isLeftClick())
                    new MatchDetailsMenu(profileMatchHistory.getFighter(), profileMatchHistory.getOpponent()).openMenu(player);
                else new RevertEloMenu(profileMatchHistory).openMenu(player);
            } else {
                new MatchDetailsMenu(profileMatchHistory.getFighter(), profileMatchHistory.getOpponent()).openMenu(player);
            }
        }

    }

}
