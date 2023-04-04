package scha.efer.technique.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.menu.EventSelectKitMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"lms host"}, permission = "technique.lms.host")
public class LMSHostCommand {

    public static void execute(Player player) {
        new EventSelectKitMenu("LMS").openMenu(player);
    }

}
