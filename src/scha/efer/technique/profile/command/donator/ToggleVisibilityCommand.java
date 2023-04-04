package scha.efer.technique.profile.command.donator;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tpv", "toggleplayervisibility"}, permission = "technique.sock")
public class ToggleVisibilityCommand {

    public void execute(Player player) {

        Profile profile = Profile.getByUuid(player);

        if (profile.getLastRunVisibility() == 0L || (System.currentTimeMillis() - profile.getLastRunVisibility()) >= 5000L) {

            profile.setVisibility(!profile.isVisibility());

            boolean vis = profile.isVisibility();

            player.sendMessage(CC.translate((vis ? "&a" : "&5") + "You are " + (vis ? "now" : "no longer") + " seeing all &dEpic " + (vis ? "&a" : "&5") + "ranks and above."));

            profile.handleVisibility();

            profile.setLastRunVisibility(System.currentTimeMillis());
        } else {
            player.sendMessage(CC.translate("&5You have to wait " + ((System.currentTimeMillis() - profile.getLastRunVisibility())) / 1000) + " more seconds before running that command again.");
        }
    }

}
