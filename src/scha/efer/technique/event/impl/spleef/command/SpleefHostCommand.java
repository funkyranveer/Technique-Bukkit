package scha.efer.technique.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.spleef.Spleef;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef host"}, permission = "technique.spleef.host")
public class SpleefHostCommand {

    public static void execute(Player player) {
        if (TechniquePlugin.get().getSpleefManager().getActiveSpleef() != null) {
            player.sendMessage(CC.RED + "There is already an active Spleef Event.");
            return;
        }

        if (!TechniquePlugin.get().getSpleefManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is an active cooldown for the Spleef Event.");
            return;
        }

        TechniquePlugin.get().getSpleefManager().setActiveSpleef(new Spleef(player));

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
