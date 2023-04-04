package scha.efer.technique.profile.command.staff;

import com.qrakn.honcho.command.*;
import org.bukkit.entity.*;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;

@CommandMeta(label = { "unfollow" }, permission = "technique.command.unfollow")
public class UnFollowCommand
{
    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!profile.isFollowMode()) {
            player.sendMessage(CC.translate("&5You aren't following anybody."));
            return;
        }

        Profile.getByUuid(profile.getFollowing().getUniqueId()).getFollower().remove(player);
        profile.setFollowMode(false);
        profile.setSilent(false);
        profile.setFollowing(null);

        player.sendMessage(CC.translate("&fYou have &5exited &ffollow mode."));
    }
}
