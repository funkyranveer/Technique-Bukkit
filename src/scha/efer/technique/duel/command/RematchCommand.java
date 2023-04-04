package scha.efer.technique.duel.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.duel.menu.DuelSelectKitMenu;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.meta.ProfileRematchData;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "rematch")
public class RematchCommand {

    public void execute(Player player) {
        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot duel a player while being frozen.");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getRematchData() == null) {
            player.sendMessage(CC.RED + "You do not have anyone to rematch.");
            return;
        }

        profile.checkForHotbarUpdate();

        if (profile.getRematchData() == null) {
            player.sendMessage(CC.RED + "That player is no longer available.");
            return;
        }

        ProfileRematchData profileRematchData = profile.getRematchData();

        if (profileRematchData.isReceive()) {
            profileRematchData.accept();
        } else {
            if (profileRematchData.isSent()) {
                player.sendMessage(CC.RED + "You have already sent a rematch request to that player.");
                return;
            }
            new DuelSelectKitMenu("rematch").openMenu(player);
            //profileRematchData.request();
        }
    }

}
