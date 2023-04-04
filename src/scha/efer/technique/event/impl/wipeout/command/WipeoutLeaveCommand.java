package scha.efer.technique.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "wipeout leave")
public class WipeoutLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Wipeout activeWipeout = TechniquePlugin.get().getWipeoutManager().getActiveWipeout();

        if (activeWipeout == null) {
            player.sendMessage(CC.RED + "There isn't any active Wipeout Events.");
            return;
        }

        if (!profile.isInWipeout() || !activeWipeout.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active Wipeout Event.");
            return;
        }

        TechniquePlugin.get().getWipeoutManager().getActiveWipeout().handleLeave(player);
    }

}
