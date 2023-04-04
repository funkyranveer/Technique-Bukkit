package scha.efer.technique.profile.command.staff;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandMeta(label = {"follow"}, permission = "technique.command.follow")
public class FollowCommand {
    public void execute(final Player player, @CPL("player") final Player target) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isFollowMode()) {
            player.sendMessage(CC.translate("&cYou are already following somebody, /unfollow before following someone again."));
            return;
        }
        profile.setFollowMode(true);
        profile.setSilent(true);
        profile.setFollowing(target);
        Profile.getByUuid(target.getUniqueId()).getFollower().add(player);
        player.sendMessage(CC.translate("&eYou have &astarted &efollowing &d" + target.getName() + "&e."));

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        if (targetProfile.isInSomeSortOfFight()) {
            if (targetProfile.isInMatch()) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(TechniquePlugin.get(), () -> player.chat("/spec " + target.getName()), 20L);
            }
        }
    }
}
