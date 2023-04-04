package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandMeta(label = {"clan forcejoin"}, async = true, permission = "clan.admin")
public class ClanForceJoinCommand {
    public void execute(Player player, String clanName) {
        if (Clan.getByMember(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "You are already in a clan");
            return;
        }

        Clan clan = Clan.getClan(clanName);
        if (clan == null) {
            UUID target = Bukkit.getOfflinePlayer(clanName).getUniqueId();
            if (target != null) {
                clan = Clan.getByMember(target);
            }
        }
        if (clan == null) {
            player.sendMessage("No Such clan.");
            return;
        }
        clan.join(player);
        player.sendMessage(ChatColor.WHITE + "You have forcefully joined clan " + ChatColor.DARK_PURPLE + clan.getName());
        for (Player others : clan.getPlayerWhereOnline()) {
            others.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " has forcefully joined your clan.");
        }
    }
}
