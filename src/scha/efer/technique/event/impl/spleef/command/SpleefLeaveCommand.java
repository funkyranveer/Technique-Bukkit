package scha.efer.technique.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.spleef.Spleef;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef leave")
public class SpleefLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Spleef activeSpleef = TechniquePlugin.get().getSpleefManager().getActiveSpleef();

        if (activeSpleef == null) {
            player.sendMessage(CC.RED + "There isn't any active Spleef Events.");
            return;
        }

        if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active Spleef Event.");
            return;
        }

        TechniquePlugin.get().getSpleefManager().getActiveSpleef().handleLeave(player);
    }

}
