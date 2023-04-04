package scha.efer.technique.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cancel", permission = "technique.sumo.cancel")
public class SumoCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getSumoManager().getActiveSumo() == null) {
            sender.sendMessage(CC.RED + "There isn't an active Sumo Event.");
            return;
        }

        TechniquePlugin.get().getSumoManager().getActiveSumo().end();
    }

}
