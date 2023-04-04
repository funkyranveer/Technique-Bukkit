package scha.efer.technique.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"juggernaut host"}, permission = "technique.juggernaut.host")
public class JuggernautHostCommand {

    public static void execute(Player player) {
        if (TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut() != null) {
            player.sendMessage(CC.RED + "There is already an active Juggernaut Event.");
            return;
        }

        if (!TechniquePlugin.get().getJuggernautManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is an active cooldown for the Juggernaut Event.");
            return;
        }

        TechniquePlugin.get().getJuggernautManager().setActiveJuggernaut(new Juggernaut(player));

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
