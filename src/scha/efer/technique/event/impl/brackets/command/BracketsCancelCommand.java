package scha.efer.technique.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "brackets cancel", permission = "technique.brackets.cancel")
public class BracketsCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getBracketsManager().getActiveBrackets() == null) {
            sender.sendMessage(CC.RED + "There isn't an active Brackets event.");
            return;
        }

        TechniquePlugin.get().getBracketsManager().getActiveBrackets().end();
    }

}
