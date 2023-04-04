package scha.efer.technique.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef tp", permission = "technique.spleef.tp")
public class SpleefTpCommand {

    public void execute(Player player) {
        player.teleport(TechniquePlugin.get().getSpleefManager().getSpleefSpectator());
        player.sendMessage(CC.GREEN + "Teleported to spleef's spawn location.");
    }

}
