package scha.efer.technique.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"parkour host"}, permission = "technique.parkour.host")
public class ParkourHostCommand {

    public static void execute(Player player) {
        if (TechniquePlugin.get().getParkourManager().getActiveParkour() != null) {
            player.sendMessage(CC.RED + "There is already an active Parkour Event.");
            return;
        }

        if (!TechniquePlugin.get().getParkourManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is an active cooldown for the Parkour Event.");
            return;
        }

        TechniquePlugin.get().getParkourManager().setActiveParkour(new Parkour(player));

        for (Player other : TechniquePlugin.get().getServer().getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(other.getUniqueId());

            if (profile.isInLobby()) {
                if (!profile.getKitEditor().isActive()) {
                    profile.refreshHotbar();
                }
            }
        }
    }

}
