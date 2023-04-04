package scha.efer.technique.profile.command.donator;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "fly", permission = "essentials.fly")
public class FlyCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if ((profile.isInLobby() || profile.isInQueue()) || profile.getPlayer().hasPermission("technique.staff")) {
            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.updateInventory();
                player.sendMessage(CC.RED + "You are no longer flying.");
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.updateInventory();
                player.sendMessage(CC.GREEN + "You are now flying.");
            }
        } else {
            player.sendMessage(CC.RED + "You cannot fly right now.");
        }
    }

}
