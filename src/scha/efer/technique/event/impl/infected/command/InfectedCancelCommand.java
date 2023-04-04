package scha.efer.technique.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "infected cancel", permission = "technique.infected.cancel")
public class InfectedCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getInfectedManager().getActiveInfected() == null) {
            sender.sendMessage(CC.RED + "There isn't an active Infected event.");
            return;
        }

        TechniquePlugin.get().getInfectedManager().getActiveInfected().end("None");
    }

}
