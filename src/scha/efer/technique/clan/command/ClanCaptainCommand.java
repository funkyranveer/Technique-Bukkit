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

@CommandMeta(label = {"clan captain"}, async = true)
public class ClanCaptainCommand {
    public void execute(Player player, String targetName) {
        if (Clan.getByMember(player.getUniqueId()) == null) {
            player.sendMessage(Color.translate("&5You do not have a clan"));
            return;
        }
        Clan clan = Clan.getByMember(player.getUniqueId());
        ClanPlayer me = clan.getClanPlayer(player.getUniqueId());
        if (me.getRole() != ClanRole.LEADER) {
            player.sendMessage(ChatColor.DARK_PURPLE + "You cannot do this with this role");
            return;
        }
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

        if (targetClanPlayer.getRole() == ClanRole.LEADER) {
            player.sendMessage(ChatColor.DARK_PURPLE + "You can not demote a leader!");
            return;
        }
        targetClanPlayer.setRole(ClanRole.CAPTAIN);
        clan.broadcast(Color.translate("&a" + targetName + ChatColor.WHITE + " has been promoted to captain!"));
        clan.saveAsync();
    }
}
