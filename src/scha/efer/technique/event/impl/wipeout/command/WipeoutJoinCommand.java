package scha.efer.technique.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.event.impl.wipeout.WipeoutState;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "wipeout join")
public class WipeoutJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Wipeout activeWipeout = TechniquePlugin.get().getWipeoutManager().getActiveWipeout();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the wipeout right now.");
            return;
        }

        if (activeWipeout == null) {
            player.sendMessage(CC.RED + "There isn't any active Wipeout Events right now.");
            return;
        }

        if (activeWipeout.getState() != WipeoutState.WAITING) {
            player.sendMessage(CC.RED + "This Wipeout Event is currently on-going and cannot be joined.");
            return;
        }

        TechniquePlugin.get().getWipeoutManager().getActiveWipeout().handleJoin(player);
    }

}
