package scha.efer.technique.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cooldown", permission = "technique.parkour.cooldown")
public class ParkourCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getParkourManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a Parkour Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the Parkour Event cooldown.");

        TechniquePlugin.get().getParkourManager().setCooldown(new Cooldown(0));
    }

}
