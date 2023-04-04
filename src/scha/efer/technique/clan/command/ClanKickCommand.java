package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.clan.ClanRole;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandMeta(label = {"clan kick"}, async = true)
public class ClanKickCommand {
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


        Profile profile = Profile.getByUuid(target);
        if (profile != null && profile.getMatch() != null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "You can not kick a player in a match!");
            return;
        }

        for (Player players : clan.getPlayerWhereOnline()) {
            players.sendMessage("&5" + targetName + " &fhas been kicked by &a" + player.getName());
        }

        clan.leave(target);
        player.sendMessage(ChatColor.WHITE + "You have kicked " + ChatColor.DARK_PURPLE + targetName);
    }
}
