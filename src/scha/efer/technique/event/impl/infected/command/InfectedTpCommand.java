package scha.efer.technique.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "infected tp", permission = "technique.parkour.tp")
public class InfectedTpCommand {

    public void execute(Player player) {
        player.teleport(TechniquePlugin.get().getInfectedManager().getInfectedSpawn1());
        player.sendMessage(CC.GREEN + "Teleported to infected's spawn location.");
    }

}
