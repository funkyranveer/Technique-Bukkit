package scha.efer.technique.event;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.menu.EventSelectEventMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"event", "events", "hostevent", "hostevents"})
public class EventCommand {

    public void execute(Player player) {
        new EventSelectEventMenu().openMenu(player);
    }
}
