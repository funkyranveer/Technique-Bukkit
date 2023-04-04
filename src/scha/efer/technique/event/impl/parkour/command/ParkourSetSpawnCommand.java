package scha.efer.technique.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour setspawn", permission = "technique.parkour.setspawn")
public class ParkourSetSpawnCommand {

    public void execute(Player player) {
        TechniquePlugin.get().getParkourManager().setParkourSpawn(player.getLocation());

        player.sendMessage(CC.GREEN + "Updated parkour's spawn location.");

        TechniquePlugin.get().getParkourManager().save();
    }

}
