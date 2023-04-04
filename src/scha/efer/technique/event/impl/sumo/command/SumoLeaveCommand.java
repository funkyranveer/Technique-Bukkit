package scha.efer.technique.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.sumo.Sumo;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo leave")
public class SumoLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Sumo activeSumo = TechniquePlugin.get().getSumoManager().getActiveSumo();

        if (activeSumo == null) {
            player.sendMessage(CC.RED + "There isn't an active Sumo Event.");
            return;
        }

        if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active Sumo Event.");
            return;
        }

        TechniquePlugin.get().getSumoManager().getActiveSumo().handleLeave(player);
    }

}
