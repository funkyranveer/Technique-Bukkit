package scha.efer.technique.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.skywars.SkyWars;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"skywars host"}, permission = "technique.skywars.host")
public class SkyWarsHostCommand {

    public static void execute(Player player) {
        if (TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars() != null) {
            player.sendMessage(CC.RED + "There is already an active SkyWars Event.");
            return;
        }

        if (!TechniquePlugin.get().getSkyWarsManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is an active cooldown for the SkyWars Event.");
            return;
        }

        TechniquePlugin.get().getSkyWarsManager().setActiveSkyWars(new SkyWars(player));

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
