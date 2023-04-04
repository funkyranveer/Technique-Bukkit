package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.util.Color;
import org.bukkit.entity.Player;

@CommandMeta(label = {"clan create"})
public class ClanCreateCommand {
    public void execute(Player player, String name) {
        if (Clan.getByMember(player.getUniqueId()) != null) {
            player.sendMessage(Color.translate("&5You are already in a clan."));
            return;
        }
        if (!Clan.checkValidityAndSend(player, name)) {
            return;
        }

        Clan.create(player, name);
        player.sendMessage(Color.translate("&aSuccessfully created your clan!"));
    }
}
