package scha.efer.technique.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "infected cooldown", permission = "technique.infected.cooldown")
public class InfectedCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getInfectedManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a Infected Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the Infected Event cooldown.");

        TechniquePlugin.get().getInfectedManager().setCooldown(new Cooldown(0));
    }

}
