package scha.efer.technique.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "wipeout setspawn", permission = "technique.wipeout.setspawn")
public class WipeoutSetSpawnCommand {

    public void execute(Player player) {
        TechniquePlugin.get().getWipeoutManager().setWipeoutSpawn(player.getLocation());

        player.sendMessage(CC.GREEN + "Updated wipeout's spawn location.");

        TechniquePlugin.get().getWipeoutManager().save();
    }

}
