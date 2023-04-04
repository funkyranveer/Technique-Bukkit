package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.match.MatchSnapshot;
import scha.efer.technique.match.menu.MatchDetailsMenu;
import scha.efer.technique.util.Color;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandMeta(label = {"clan showinv"}, async = false)
public class ClanShowInvCommand {
    public void execute(Player player, String targetName) {
        String[] data = targetName.split(" ");
        Clan clan = Clan.getClan(data[0]);
        if (clan == null) {
            player.sendMessage(Color.translate("&5There were no clans with the name " + data[0] + "."));
            return;
        }
        MatchSnapshot snapshot = clan.getMatchSnapShot(UUID.fromString(data[1]));
        if (snapshot == null) {
            player.sendMessage(Color.translate("&5The inventory you are looking for could not be found."));
            return;
        }
        new MatchDetailsMenu(snapshot, null).openMenu(player);
    }
}
