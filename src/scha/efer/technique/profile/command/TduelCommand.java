package scha.efer.technique.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tduel", "toggleduel", "dueltoggle"})
public class TduelCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean scoreboardstate = profile.getOptions().isReceiveDuelRequests();
        player.sendMessage(CC.translate("&fReceiving Duels: " + (!scoreboardstate ? "&aOn" : "&5Off")));
        profile.getOptions().setReceiveDuelRequests(!scoreboardstate);
    }

}
