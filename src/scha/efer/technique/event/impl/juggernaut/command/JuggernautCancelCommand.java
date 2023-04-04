package scha.efer.technique.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "juggernaut cancel", permission = "technique.juggernaut.cancel")
public class JuggernautCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut() == null) {
            sender.sendMessage(CC.RED + "There isn't an active Juggernaut event.");
            return;
        }

        TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().end("None");
    }

}
