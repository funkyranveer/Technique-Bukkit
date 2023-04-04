package scha.efer.technique.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.lms.LMS;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms leave")
public class LMSLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = TechniquePlugin.get().getLMSManager().getActiveLMS();

        if (activeLMS == null) {
            player.sendMessage(CC.RED + "There isn't any active LMS Events.");
            return;
        }

        if (!profile.isInLMS() || !activeLMS.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active LMS Event.");
            return;
        }

        TechniquePlugin.get().getLMSManager().getActiveLMS().handleLeave(player);
    }

}
