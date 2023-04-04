package scha.efer.technique.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.lms.LMS;
import scha.efer.technique.event.impl.lms.LMSState;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms join")
public class LMSJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = TechniquePlugin.get().getLMSManager().getActiveLMS();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the lms right now.");
            return;
        }

        if (activeLMS == null) {
            player.sendMessage(CC.RED + "There isn't any active LMS Events right now.");
            return;
        }

        if (activeLMS.getState() != LMSState.WAITING) {
            player.sendMessage(CC.RED + "This LMS Event is currently on-going and cannot be joined.");
            return;
        }

        TechniquePlugin.get().getLMSManager().getActiveLMS().handleJoin(player);
    }

}
