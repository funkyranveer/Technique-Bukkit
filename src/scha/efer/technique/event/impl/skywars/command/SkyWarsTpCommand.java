package scha.efer.technique.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.LocationUtil;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars tp", permission = "technique.skywars.tp")
public class SkyWarsTpCommand {

    public void execute(Player player) {
        player.teleport(LocationUtil.deserialize(TechniquePlugin.get().getSkyWarsManager().getSkyWarsSpectators().get(0)));
        player.sendMessage(CC.GREEN + "Teleported to skywars's spawn location.");
    }

}
