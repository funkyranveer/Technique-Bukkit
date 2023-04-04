package scha.efer.technique.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.entity.Player;

@CommandMeta(label = {"schaeferpractice", "prac", "practice", "help", "?"})
public class PracticeCommand {

    public void execute(Player player) {
        player.sendMessage(" ");
        player.sendMessage(Color.translate("&dUseful Commands:"));
        player.sendMessage(Color.translate("&e/duel <Name> &7- &dSend a player a duel request"));
        player.sendMessage(Color.translate("&e/spec <Name> &7- &dSpectate a player's ongoing match"));
        player.sendMessage(Color.translate("&e/request <Reason> &7- &dRequest assistance from staff"));
        player.sendMessage(Color.translate("&e/party &7- &dSee all party related commands"));
        player.sendMessage(Color.translate("&e/clan &7- &dList of all Clan Commands"));
        player.sendMessage(" ");
        player.sendMessage(Color.translate("&dUseful Links:"));
        player.sendMessage(Color.translate("&eWebsite &7- &dstrafe.world"));
        player.sendMessage(Color.translate("&eDiscord &7- &ddiscord.strafe.world"));
        player.sendMessage(Color.translate("&eStore &7- &dstore.strafe.world"));
        player.sendMessage(Color.translate("&eTeamspeak &7- &dts.strafe.world"));
        player.sendMessage(" ");
    }
}
