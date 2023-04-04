package scha.efer.technique.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cooldown", permission = "technique.sumo.cooldown")
public class SumoCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getSumoManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't any cooldown for the Sumo Event.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the Sumo Event cooldown.");

        TechniquePlugin.get().getSumoManager().setCooldown(new Cooldown(0));
    }

}
