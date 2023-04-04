package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.clan.ClanRole;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"clan desc", "clan description"})
public class ClanDescCommand {
    public void execute(Player player, String message) {
        if (Clan.getByMember(player.getUniqueId()) == null) {
            player.sendMessage(Color.translate("&5You are not in a clan"));
            return;
        }

        Clan clan = Clan.getByMember(player.getUniqueId());
        ClanPlayer me = clan.getClanPlayer(player.getUniqueId());
        if (me.getRole() != ClanRole.LEADER && me.getRole() != ClanRole.CAPTAIN) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Only clan leaders and captains may do this");
            return;
        }
        if (message.length() > 128) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Clan descirption is limited to 128 characters.");
            return;
        }
        clan.setDescription(message);
        player.sendMessage(Color.translate("&aYour clan description has been updated"));
    }
}
