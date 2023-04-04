package scha.efer.technique.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tsb", "togglesb", "sb toggle", "togglesidebar"})
public class TsbCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean scoreboardstate = profile.getOptions().isShowScoreboard();
        player.sendMessage(CC.translate("&fScoreboard: " + (!scoreboardstate ? "&aOn" : "&5Off")));
        profile.getOptions().setShowScoreboard(!scoreboardstate);
    }

}
