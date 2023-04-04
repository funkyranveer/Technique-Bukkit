package scha.efer.technique.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.LocationUtil;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars setspawn", permission = "technique.skywars.setspawn")
public class SkyWarsSetSpawnCommand {

    public void execute(Player player) {
        TechniquePlugin.get().getSkyWarsManager().getSkyWarsSpectators().add(LocationUtil.serialize(player.getLocation()));

        player.sendMessage(CC.GREEN + "Added skywars's spawn location.");

        TechniquePlugin.get().getSkyWarsManager().save();
    }

}
