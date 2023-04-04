package scha.efer.technique.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets tp", permission = "technique.brackets.tp")
public class BracketsTpCommand {

    public void execute(Player player) {
        player.teleport(TechniquePlugin.get().getBracketsManager().getBracketsSpectator());
        player.sendMessage(CC.GREEN + "Teleported to brackets's spawn location.");
    }

}
