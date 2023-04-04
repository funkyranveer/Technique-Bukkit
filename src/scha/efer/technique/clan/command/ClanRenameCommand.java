package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.clan.ClanRole;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"clan rename"})
public class ClanRenameCommand {
    public void execute(Player player, String name) {
        if (Clan.getByMember(player.getUniqueId()) == null) {
            player.sendMessage(Color.translate("&5You are not in a clan"));
            return;
        }

        Clan clan = Clan.getByMember(player.getUniqueId());
        ClanPlayer me = clan.getClanPlayer(player.getUniqueId());
        if (me.getRole() != ClanRole.LEADER) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Only clan leaders may do this");
            return;
        }

        if (!Clan.checkValidityAndSend(player, name)) {
            return;
        }
        clan.setName(name);
        player.sendMessage(Color.translate("&aYour clan name has been updated"));
    }
}
