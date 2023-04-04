package scha.efer.technique.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.kit.Kit;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "kit list", permission = "technique.kit.list")
public class KitListCommand {

    public void execute(CommandSender sender) {
        for (Kit kit : Kit.getKits()) {
            sender.sendMessage(kit.getName());
        }
    }

}
