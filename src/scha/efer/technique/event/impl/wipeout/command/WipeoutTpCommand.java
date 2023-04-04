package scha.efer.technique.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "wipeout tp", permission = "technique.wipeout.tp")
public class WipeoutTpCommand {

    public void execute(Player player) {
        player.teleport(TechniquePlugin.get().getWipeoutManager().getWipeoutSpawn());
        player.sendMessage(CC.GREEN + "Teleported to wipeout's spawn location.");
    }

}
