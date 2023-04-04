package scha.efer.technique.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef setspawn", permission = "technique.spleef.setspawn")
public class SpleefSetSpawnCommand {

    public void execute(Player player) {
        TechniquePlugin.get().getSpleefManager().setSpleefSpectator(player.getLocation());

        player.sendMessage(CC.GREEN + "Set spleef's spawn location.");

        TechniquePlugin.get().getSpleefManager().save();
    }

}
