package scha.efer.technique.clan.command;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanMatchHistory;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.util.Color;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.TimeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandMeta(label = {"clan info"}, async = true)
public class ClanInfoCommand {
    private static final String CIRCLE = "●";

    public void execute(Player player) {
        if (Clan.getByMember(player.getUniqueId()) == null) {
            player.sendMessage(Color.translate(ChatColor.DARK_PURPLE + "You dont belong to a clan."));
            return;
        }

        Clan clan = Clan.getByMember(player.getUniqueId());
        printdetails(player, clan);


    }

    public void execute(Player player, String clanName) {
        Clan clan = Clan.getClan(clanName);
        if (clan == null) {
            UUID target = Bukkit.getOfflinePlayer(clanName).getUniqueId();
            if (target != null) {
                clan = Clan.getByMember(target);
            }
        }
        if (clan == null) {
            player.sendMessage(Color.translate("&5There were no clans with the name or player " + ChatColor.BOLD + clanName + "."));
            return;
        }
        printdetails(player, clan);
    }

    private void printdetails(Player player, Clan clan) {
        player.sendMessage(org.bukkit.ChatColor.WHITE.toString() + org.bukkit.ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&l" + clan.getName() + " &f(" + clan.getMembers().size() + "/" + "5)"));
        String description = clan.getDescription();
        if (description == null) {
            description = (Color.translate("&fThis is a default description"));
        }
        player.sendMessage(Color.translate("&5Description: &f" + description));
        player.sendMessage(Color.translate("&5Clans Elo: " + Color.translate("&f" + clan.getElo())));
        player.sendMessage(Color.translate("&5Clans Wins: " + Color.translate("&f" + clan.getWins())));
        player.sendMessage(Color.translate("&5Clans Losses: " + Color.translate("&f" + clan.getLoses())));
        List<ClanPlayer> players = Lists.newArrayList(clan.getMembers());
        players.sort(Comparator.comparing(ClanPlayer::getName));

        players.sort(Comparator.comparingInt(o -> o.getRole().ordinal()));

        String members = Joiner.on(", ").join(players.stream().map(f ->
                (Bukkit.getPlayer(f.getUniqueId()) == null ? ChatColor.WHITE : ChatColor.GREEN) + f.getRole().getPrefix() + f.getName()).collect(Collectors.toList()));

        player.sendMessage(Color.translate("&5Members: " + Color.translate("&f" + members)));
        player.sendMessage(Color.translate("&5Date Created: " + Color.translate("&f" + TimeUtil.dateToString(clan.getCreatedAt()))));

        ComponentBuilder builder = new ComponentBuilder("Match History: ").color(ChatColor.DARK_PURPLE);
        for (ClanMatchHistory clanMatchHistory : clan.getMatchhistory()) {
            builder.append(CIRCLE + " ");
            builder.color(clanMatchHistory.isWon() ? ChatColor.GREEN : ChatColor.DARK_PURPLE);
            List<String> hover = Lists.newArrayList();
            hover.add("Clan Member Fought: " + CC.translate(clanMatchHistory.getFighter().getTeamPlayer().getDisplayName()));
            hover.add("Opponent: " + CC.translate(clanMatchHistory.getOpponent().getTeamPlayer().getDisplayName()));
            hover.add("Result: " + (clanMatchHistory.isWon() ? "Won" : "Lost"));
            String eloChange;
            if (clanMatchHistory.isWon()) {
                eloChange = "+" + clanMatchHistory.getEloChange();
            } else {
                eloChange = "-" + clanMatchHistory.getEloChange();

            }
            hover.add("Elo Change: " + eloChange);

            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Joiner.on("\n").join(hover))));
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan showinv " + clan.getName() + " " + clanMatchHistory.getFighter().getUniqueID().toString()));

        }

        player.spigot().sendMessage(builder.create());
        player.sendMessage(org.bukkit.ChatColor.WHITE.toString() + org.bukkit.ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");

    }
}
