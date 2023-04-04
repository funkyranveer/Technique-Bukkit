package scha.efer.technique.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.menu.EventSelectKitMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"brackets host"}, permission = "technique.brackets.host")
public class BracketsHostCommand {

    public static void execute(Player player) {
        new EventSelectKitMenu("Brackets").openMenu(player);
    }

}
