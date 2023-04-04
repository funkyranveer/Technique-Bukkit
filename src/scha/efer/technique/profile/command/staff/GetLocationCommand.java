package scha.efer.technique.profile.command.staff;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ChatComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

@CommandMeta(label = "getloc", permission = "smok.staff.location")
public class GetLocationCommand {

    public void execute(Player player) {
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        double yaw = player.getLocation().getYaw();
        double pitch = player.getLocation().getPitch();

        player.sendMessage(CC.translate("&5X: " + player.getLocation().getX()));
        player.sendMessage(CC.translate("&5Y: " + player.getLocation().getY()));
        player.sendMessage(CC.translate("&5Z: " + player.getLocation().getZ()));
        player.sendMessage(CC.translate("&5YAW: " + player.getLocation().getYaw()));
        player.sendMessage(CC.translate("&5PITCH: " + player.getLocation().getPitch()));
        player.spigot().sendMessage(new ChatComponentBuilder("")
                .parse("&a(Click to copy)")
                .attachToEachPart(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, x + " " + y + " " + z + " " + yaw + " " + pitch))
                .create());

    }
}
