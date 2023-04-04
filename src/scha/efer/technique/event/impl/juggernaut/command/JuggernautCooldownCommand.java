package scha.efer.technique.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "juggernaut cooldown", permission = "technique.juggernaut.cooldown")
public class JuggernautCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getJuggernautManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a Juggernaut Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the Juggernaut Event cooldown.");

        TechniquePlugin.get().getJuggernautManager().setCooldown(new Cooldown(0));
    }

}
