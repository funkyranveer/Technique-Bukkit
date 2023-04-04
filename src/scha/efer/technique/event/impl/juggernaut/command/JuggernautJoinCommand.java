package scha.efer.technique.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.event.impl.juggernaut.JuggernautState;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "juggernaut join")
public class JuggernautJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Juggernaut activeJuggernaut = TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the juggernaut right now.");
            return;
        }

        if (activeJuggernaut == null) {
            player.sendMessage(CC.RED + "There isn't any active Juggernaut Events right now.");
            return;
        }

        if (activeJuggernaut.getState() != JuggernautState.WAITING) {
            player.sendMessage(CC.RED + "This Juggernaut Event is currently on-going and cannot be joined.");
            return;
        }

        TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().handleJoin(player);
    }

}
