package scha.efer.technique.profile.stats.menu;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.TimeUtil;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class ProfileMenu extends Menu {

    private final Player target;

    @Override
    public String getTitle(Player player) {
        return "&d" + target.getName() + "'&es Global Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(1, new GlobalStatsButton());
        buttons.put(3, new EloStatsButton());
        buttons.put(5, new MatchStatsButton());
        buttons.put(7, new ClanStatsButton());

        return buttons;
    }

    @AllArgsConstructor
    private class GlobalStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());

            lore.add("&8&m-------------------------------");
            lore.add(" &5ELO: &r" + profile.getGlobalElo());
            lore.add(" &5League: &r" + profile.getEloLeague());
            lore.add("&8&m--------------------------");
            lore.add(" &5Ranked Wins: &r" + profile.getTotalRankedWins());
            lore.add(" &5Ranked Losses: &r" + profile.getTotalRankedLosses());
            lore.add("&8&m--------------------------");
            lore.add(" &5Unranked Wins: &r" + profile.getTotalUnrankedWins());
            lore.add(" &5Unranked Losses: &r" + profile.getTotalUnrankedLosses());
            lore.add("&8&m-------------------------------");

            return new ItemBuilder(Material.BEACON)
                    .name("&5&lGlobal Stats")
                    .lore(lore)
                    .build();
        }

    }

    @AllArgsConstructor
    private class EloStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m-------------------------------");
            lore.add("&fLeft-Click to view your elo stats");
            lore.add("&8&m-------------------------------");
            return new ItemBuilder(Material.PAPER)
                    .name("&5&lElo Stats")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new ELOMenu(target).openMenu(player);
        }

    }

    @AllArgsConstructor
    private class MatchStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m-------------------------------");
            lore.add("&fLeft-Click to view recent Unranked matches");
            lore.add("&fRight-Click to view recent Ranked matches");
            lore.add("&8&m-------------------------------");
            return new ItemBuilder(Material.BOOK)
                    .name("&5&lMatch History")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            if (clickType.isLeftClick()) new MatchHistoryMenu(target, false).openMenu(player);
            else new MatchHistoryMenu(target, true).openMenu(player);
        }

    }

    @AllArgsConstructor
    private class ClanStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            if (Clan.getByMember(player.getUniqueId()) == null) {
                return new ItemBuilder(Material.GOLD_INGOT)
                        .name("&5&lClan Info")
                        .lore("&5This player does not belong to any clan")
                        .build();
            }
            Clan clan = Clan.getByMember(player.getUniqueId());

            String description = clan.getDescription();
            if (description == null) {
                description = ("&fDefault description");
            }
            lore.add("&8&m-------------------------------");
            lore.add("&5Desc: &f" + description);
            lore.add("&5Elo: " + "&f" + clan.getElo());
            lore.add("&5Wins: " + "&f" + clan.getWins());
            lore.add("&5Losses: " + "&f" + clan.getLoses());
            lore.add("&5Created: " + "&f" + TimeUtil.dateToString(clan.getCreatedAt()));
            lore.add("&8&m-------------------------------");
            List<ClanPlayer> players = Lists.newArrayList(clan.getMembers());
            Collections.sort(players, new Comparator<ClanPlayer>() {
                @Override
                public int compare(ClanPlayer o1, ClanPlayer o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            Collections.sort(players, new Comparator<ClanPlayer>() {
                @Override
                public int compare(ClanPlayer o1, ClanPlayer o2) {
                    return o1.getRole().ordinal() - o2.getRole().ordinal();
                }
            });
            List<String> members = Lists.transform(players, new Function<ClanPlayer, String>() {
                @Override
                public String apply(ClanPlayer f) {
                    return (Bukkit.getPlayer(f.getUniqueId()) == null ? "&f" : "&a") + f.getRole().getPrefix() + f.getName();
                }
            });
            lore.add("&5Members: ");
            lore.addAll(members);
            lore.add("&8&m-------------------------------");

            return new ItemBuilder(Material.DIAMOND_PICKAXE)
                    .name("&5&l" + clan.getName() + " &f[" + clan.getPlayerWhereOnline().size() + "/" + clan.getMembers().size() + "]")
                    .lore(lore)
                    .build();
        }

    }

}
