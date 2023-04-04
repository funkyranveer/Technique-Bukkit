package scha.efer.technique.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.sumo.Sumo;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo host"}, permission = "technique.sumo.host")
public class SumoHostCommand {

    public static void execute(Player player) {
        if (TechniquePlugin.get().getSumoManager().getActiveSumo() != null) {
            player.sendMessage(CC.RED + "There is already an active Sumo Event.");
            return;
        }

        if (!TechniquePlugin.get().getSumoManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is a Sumo Event cooldown active.");
            return;
        }

        TechniquePlugin.get().getSumoManager().setActiveSumo(new Sumo(player));

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
