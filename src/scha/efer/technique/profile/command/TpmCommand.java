package scha.efer.technique.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tpm", "togglepm", "togglemsg", "msgtoggle"})
public class TpmCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean scoreboardstate = profile.getOptions().isPrivateMessages();
        player.sendMessage(CC.translate("&fPrivate Messages: " + (!scoreboardstate ? "&aOn" : "&5Off")));
        profile.getOptions().setPrivateMessages(!scoreboardstate);
    }

}
