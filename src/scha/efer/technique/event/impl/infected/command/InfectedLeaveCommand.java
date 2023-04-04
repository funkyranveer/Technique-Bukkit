package scha.efer.technique.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.infected.Infected;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "infected leave")
public class InfectedLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Infected activeInfected = TechniquePlugin.get().getInfectedManager().getActiveInfected();

        if (activeInfected == null) {
            player.sendMessage(CC.RED + "There isn't any active Infected Events.");
            return;
        }

        if (!profile.isInInfected() || !activeInfected.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active Infected Event.");
            return;
        }

        TechniquePlugin.get().getInfectedManager().getActiveInfected().handleLeave(player);
    }

}
