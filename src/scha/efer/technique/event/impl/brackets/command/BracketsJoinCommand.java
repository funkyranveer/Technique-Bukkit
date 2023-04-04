package scha.efer.technique.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.brackets.Brackets;
import scha.efer.technique.event.impl.brackets.BracketsState;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets join")
public class BracketsJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Brackets activeBrackets = TechniquePlugin.get().getBracketsManager().getActiveBrackets();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the brackets right now.");
            return;
        }

        if (activeBrackets == null) {
            player.sendMessage(CC.RED + "There isn't any active Brackets Events right now.");
            return;
        }

        if (activeBrackets.getState() != BracketsState.WAITING) {
            player.sendMessage(CC.RED + "This Brackets Event is currently on-going and cannot be joined.");
            return;
        }

        TechniquePlugin.get().getBracketsManager().getActiveBrackets().handleJoin(player);
    }

}
