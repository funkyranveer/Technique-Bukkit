package scha.efer.technique.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "wipeout cooldown", permission = "technique.wipeout.cooldown")
public class WipeoutCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getWipeoutManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a Wipeout Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the Wipeout Event cooldown.");

        TechniquePlugin.get().getWipeoutManager().setCooldown(new Cooldown(0));
    }

}
