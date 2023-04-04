package scha.efer.technique.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "skywars cancel", permission = "technique.skywars.cancel")
public class SkyWarsCancelCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars() == null) {
            sender.sendMessage(CC.RED + "There isn't an active SkyWars event.");
            return;
        }

        TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars().end();
    }

}
