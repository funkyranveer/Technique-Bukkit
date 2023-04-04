package scha.efer.technique.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour leave")
public class ParkourLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Parkour activeParkour = TechniquePlugin.get().getParkourManager().getActiveParkour();

        if (activeParkour == null) {
            player.sendMessage(CC.RED + "There isn't any active Parkour Events.");
            return;
        }

        if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active Parkour Event.");
            return;
        }

        TechniquePlugin.get().getParkourManager().getActiveParkour().handleLeave(player);
    }

}
