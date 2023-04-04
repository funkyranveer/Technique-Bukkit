package scha.efer.technique.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.spleef.Spleef;
import scha.efer.technique.event.impl.spleef.SpleefState;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef join")
public class SpleefJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Spleef activeSpleef = TechniquePlugin.get().getSpleefManager().getActiveSpleef();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the spleef right now.");
            return;
        }

        if (activeSpleef == null) {
            player.sendMessage(CC.RED + "There isn't any active Spleef Events right now.");
            return;
        }

        if (activeSpleef.getState() != SpleefState.WAITING) {
            player.sendMessage(CC.RED + "This Spleef Event is currently on-going and cannot be joined.");
            return;
        }

        TechniquePlugin.get().getSpleefManager().getActiveSpleef().handleJoin(player);
    }

}
