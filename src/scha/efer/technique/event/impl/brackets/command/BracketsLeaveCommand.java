package scha.efer.technique.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.brackets.Brackets;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets leave")
public class BracketsLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Brackets activeBrackets = TechniquePlugin.get().getBracketsManager().getActiveBrackets();

        if (activeBrackets == null) {
            player.sendMessage(CC.RED + "There isn't any active Brackets Events.");
            return;
        }

        if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active Brackets Event.");
            return;
        }

        TechniquePlugin.get().getBracketsManager().getActiveBrackets().handleLeave(player);
    }

}
