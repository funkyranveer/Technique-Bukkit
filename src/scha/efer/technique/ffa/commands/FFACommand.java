package scha.efer.technique.ffa.commands;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.ffa.FFA;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "joinffa")
public class FFACommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getState() == ProfileState.IN_EVENT || profile.getState() == ProfileState.IN_QUEUE || profile.getState() == ProfileState.IN_FIGHT || profile.getState() == ProfileState.SPECTATE_MATCH) {
            player.sendMessage(CC.RED + "You can't join ffa while your current status.");
            return;
        }

        if (profile.getState() == ProfileState.IN_FFA) {
            player.sendMessage(CC.RED + "You're already in ffa.");
            return;
        }

        FFA.handleJoin(player);
    }

}