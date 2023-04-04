package scha.efer.technique.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms tp", permission = "technique.lms.tp")
public class LMSTpCommand {

    public void execute(Player player) {
        player.teleport(TechniquePlugin.get().getLMSManager().getLmsSpectator());
        player.sendMessage(CC.GREEN + "Teleported to lms's spawn location.");
    }

}
