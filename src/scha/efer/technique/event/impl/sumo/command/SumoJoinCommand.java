package scha.efer.technique.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.sumo.Sumo;
import scha.efer.technique.event.impl.sumo.SumoState;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo join")
public class SumoJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Sumo activeSumo = TechniquePlugin.get().getSumoManager().getActiveSumo();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the Sumo Event right now.");
            return;
        }

        if (activeSumo == null) {
            player.sendMessage(CC.RED + "There isn't an active Sumo Event.");
            return;
        }

        if (activeSumo.getState() != SumoState.WAITING) {
            player.sendMessage(CC.RED + "That Sumo Event is currently on-going and cannot be joined.");
            return;
        }

        TechniquePlugin.get().getSumoManager().getActiveSumo().handleJoin(player);
    }

}
