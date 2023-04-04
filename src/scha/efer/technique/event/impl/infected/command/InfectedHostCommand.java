package scha.efer.technique.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.infected.Infected;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"infected host"}, permission = "technique.infected.host")
public class InfectedHostCommand {

    public static void execute(Player player) {
        if (TechniquePlugin.get().getInfectedManager().getActiveInfected() != null) {
            player.sendMessage(CC.RED + "There is already an active Infected Event.");
            return;
        }

        if (!TechniquePlugin.get().getInfectedManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is an active cooldown for the Infected Event.");
            return;
        }

        TechniquePlugin.get().getInfectedManager().setActiveInfected(new Infected(player));

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
