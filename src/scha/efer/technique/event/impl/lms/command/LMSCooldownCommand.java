package scha.efer.technique.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "lms cooldown", permission = "technique.lms.cooldown")
public class LMSCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getLMSManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a LMS Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the LMS Event cooldown.");

        TechniquePlugin.get().getLMSManager().setCooldown(new Cooldown(0));
    }

}
