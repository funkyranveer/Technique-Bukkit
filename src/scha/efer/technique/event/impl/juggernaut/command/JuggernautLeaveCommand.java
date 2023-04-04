package scha.efer.technique.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "juggernaut leave")
public class JuggernautLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Juggernaut activeJuggernaut = TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut();

        if (activeJuggernaut == null) {
            player.sendMessage(CC.RED + "There isn't any active Juggernaut Events.");
            return;
        }

        if (!profile.isInJuggernaut() || !activeJuggernaut.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active Juggernaut Event.");
            return;
        }

        TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().handleLeave(player);
    }

}
