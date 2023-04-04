package scha.efer.technique.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour tp", permission = "technique.parkour.tp")
public class ParkourTpCommand {

    public void execute(Player player) {
        player.teleport(TechniquePlugin.get().getParkourManager().getParkourSpawn());
        player.sendMessage(CC.GREEN + "Teleported to parkour's spawn location.");
    }

}
