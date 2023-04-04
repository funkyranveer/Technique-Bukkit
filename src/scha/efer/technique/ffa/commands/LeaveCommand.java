package scha.efer.technique.ffa.commands;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.ffa.FFA;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "leave")
public class LeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getState() != ProfileState.IN_FFA) {
            player.sendMessage(CC.RED + "You are not already in ffa.");
            return;
        }

        FFA.handleLeave(player);
    }

}