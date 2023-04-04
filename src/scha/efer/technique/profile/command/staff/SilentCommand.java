package scha.efer.technique.profile.command.staff;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "silent", permission = "technique.command.silent")
public class SilentCommand {


    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isFollowMode()) {
            player.sendMessage(CC.translate("&5You are currently following somebody!"));
            return;
        }

        profile.setSilent(!profile.isSilent());

        player.sendMessage(CC.translate("&fYou have " + (profile.isSilent() ? "&aenabled" : "&5disabled") + " &fsilent mode."));
    }

    public void execute(Player player, @CPL("player") Player target) {
        Profile profile = Profile.getByUuid(target.getUniqueId());

        if (profile.isFollowMode()) {
            player.sendMessage(CC.translate("&5That person is currently following somebody!"));
            return;
        }

        profile.setSilent(!profile.isSilent());

        player.sendMessage(CC.translate("&fYou have " + (profile.isSilent() ? "&aenabled" : "&5disabled") + " &fsilent mode for " + target.getName() + "."));
    }

}
