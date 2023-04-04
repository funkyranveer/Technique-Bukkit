package scha.efer.technique.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "wipeout cancel", permission = "technique.wipeout.cancel")
public class WipeoutCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getWipeoutManager().getActiveWipeout() == null) {
            sender.sendMessage(CC.RED + "There isn't an active Wipeout event.");
            return;
        }

        TechniquePlugin.get().getWipeoutManager().getActiveWipeout().end(null);
    }

}
