package scha.efer.technique.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo tp", permission = "technique.sumo.tp")
public class SumoTpCommand {

    public void execute(Player player) {
        player.teleport(TechniquePlugin.get().getSumoManager().getSumoSpectator());
        player.sendMessage(CC.GREEN + "Teleported to sumo's spawn location.");
    }

}
