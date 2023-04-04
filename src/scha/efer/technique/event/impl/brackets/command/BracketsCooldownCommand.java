package scha.efer.technique.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "brackets cooldown", permission = "technique.brackets.cooldown")
public class BracketsCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getBracketsManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a Brackets Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the Brackets Event cooldown.");

        TechniquePlugin.get().getBracketsManager().setCooldown(new Cooldown(0));
    }

}
