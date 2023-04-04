package scha.efer.technique.arena.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.ArenaType;
import scha.efer.technique.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "arenas", permission = "technique.admin.arena")
public class ArenasCommand {

    public void execute(CommandSender sender) {
        sender.sendMessage(CC.RED + "Arenas:");

        if (Arena.getArenas().isEmpty()) {
            sender.sendMessage(CC.GRAY + "There are no arenas.");
            return;
        }

        for (Arena arena : Arena.getArenas()) {
            if (arena.getType() != ArenaType.DUPLICATE) {
                sender.sendMessage(CC.GRAY + " - " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() +
                        CC.GRAY + " (" + arena.getType().name() + ")");
            }
        }
    }

}
