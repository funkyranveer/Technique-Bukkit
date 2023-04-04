package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.clan.ClanRole;
import scha.efer.technique.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"clan leave"})
public class ClanLeaveCommand {
    public void execute(Player player) {
        if (Clan.getByMember(player.getUniqueId()) == null) {
            player.sendMessage("You are not in a clan");
            return;
        }

        Clan clan = Clan.getByMember(player.getUniqueId());
        ClanPlayer me = clan.getClanPlayer(player.getUniqueId());
        if (me.getRole() == ClanRole.LEADER) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Leaders can not leave.");
            return;
        }
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile != null && profile.getMatch() != null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "You can not leave while in a match!");
            return;
        }
        clan.leave(player.getUniqueId());
        for (Player players : clan.getPlayerWhereOnline()) {
            players.sendMessage(ChatColor.DARK_PURPLE + player.getName() + ChatColor.WHITE + " has left your clan");
        }
        player.sendMessage(ChatColor.WHITE + "You have left " + ChatColor.DARK_PURPLE + clan.getName());

    }
}
