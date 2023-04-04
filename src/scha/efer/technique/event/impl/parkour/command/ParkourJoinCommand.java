package scha.efer.technique.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.event.impl.parkour.ParkourState;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour join")
public class ParkourJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Parkour activeParkour = TechniquePlugin.get().getParkourManager().getActiveParkour();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the parkour right now.");
            return;
        }

        if (activeParkour == null) {
            player.sendMessage(CC.RED + "There isn't any active Parkour Events right now.");
            return;
        }

        if (activeParkour.getState() != ParkourState.WAITING) {
            player.sendMessage(CC.RED + "This Parkour Event is currently on-going and cannot be joined.");
            return;
        }

        TechniquePlugin.get().getParkourManager().getActiveParkour().handleJoin(player);
    }

}
