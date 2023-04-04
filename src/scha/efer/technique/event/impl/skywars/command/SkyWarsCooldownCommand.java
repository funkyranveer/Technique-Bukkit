package scha.efer.technique.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "skywars cooldown", permission = "technique.skywars.cooldown")
public class SkyWarsCooldownCommand {

    public void execute(CommandSender sender) {
        if (TechniquePlugin.get().getSkyWarsManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a SkyWars Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the SkyWars Event cooldown.");

        TechniquePlugin.get().getSkyWarsManager().setCooldown(new Cooldown(0));
    }

}
