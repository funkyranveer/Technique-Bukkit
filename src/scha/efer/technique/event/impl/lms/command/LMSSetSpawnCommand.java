package scha.efer.technique.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms setspawn", permission = "technique.lms.setspawn")
public class LMSSetSpawnCommand {

    public void execute(Player player) {
        TechniquePlugin.get().getLMSManager().setLmsSpectator(player.getLocation());

        player.sendMessage(CC.GREEN + "Updated lms's spawn location.");

        TechniquePlugin.get().getLMSManager().save();
    }

}
