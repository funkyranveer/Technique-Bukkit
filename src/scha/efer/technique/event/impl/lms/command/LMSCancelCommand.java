package scha.efer.technique.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "lms cancel", permission = "technique.lms.cancel")
public class LMSCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getLMSManager().getActiveLMS() == null) {
            sender.sendMessage(CC.RED + "There isn't an active LMS event.");
            return;
        }

        TechniquePlugin.get().getLMSManager().getActiveLMS().end();
    }

}
