package scha.efer.technique.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cancel", permission = "technique.parkour.cancel")
public class ParkourCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getParkourManager().getActiveParkour() == null) {
            sender.sendMessage(CC.RED + "There isn't an active Parkour event.");
            return;
        }

        TechniquePlugin.get().getParkourManager().getActiveParkour().end(null);
    }

}
