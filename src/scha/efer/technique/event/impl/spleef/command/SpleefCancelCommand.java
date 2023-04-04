package scha.efer.technique.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "spleef cancel", permission = "technique.spleef.cancel")
public class SpleefCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getSpleefManager().getActiveSpleef() == null) {
            sender.sendMessage(CC.RED + "There isn't an active Spleef event.");
            return;
        }

        TechniquePlugin.get().getSpleefManager().getActiveSpleef().end();
    }

}
