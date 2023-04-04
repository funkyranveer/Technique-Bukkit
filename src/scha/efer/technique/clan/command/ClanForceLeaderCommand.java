package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.clan.ClanRole;
import scha.efer.technique.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandMeta(label = {"clan forceleader"}, async = true, permission = "clan.admin")
public class ClanForceLeaderCommand {
    public void execute(Player player, String targetName) {
        if (Clan.getByMember(player.getUniqueId()) == null) {
            player.sendMessage(Color.translate("&5You do not have a clan"));
            return;
        }
        Clan clan = Clan.getByMember(player.getUniqueId());
        UUID target = Bukkit.getOfflinePlayer(targetName).getUniqueId();
        if (target == null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Your faction doesn't have a member named &5&l" + player.getName());
            return;
        }
        if (Clan.getByMember(target) != clan) {
            player.sendMessage(ChatColor.DARK_PURPLE + "That player is not a member of your clan");
            return;
        }
        ClanPlayer targetClanPlayer = clan.getClanPlayer(target);
        for (ClanPlayer clanPlayer : clan.getMembers()) {
            if (clanPlayer.getRole() == ClanRole.LEADER) {
                clanPlayer.setRole(ClanRole.CAPTAIN);
            }
        }
        targetClanPlayer.setRole(ClanRole.LEADER);
        clan.broadcast(Color.translate("&a" + targetName + ChatColor.WHITE + " has been forcefully promoted to leader!"));
        clan.saveAsync();
    }
}
