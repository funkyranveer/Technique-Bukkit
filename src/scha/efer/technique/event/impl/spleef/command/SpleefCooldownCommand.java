package scha.efer.technique.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "spleef cooldown", permission = "technique.spleef.cooldown")
public class SpleefCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getSpleefManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a Spleef Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the Spleef Event cooldown.");

        TechniquePlugin.get().getSpleefManager().setCooldown(new Cooldown(0));
    }

}
