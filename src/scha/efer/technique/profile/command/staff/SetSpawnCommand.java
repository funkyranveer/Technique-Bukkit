package scha.efer.technique.profile.command.staff;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import org.bukkit.entity.Player;
import scha.efer.technique.util.external.CC;

@CommandMeta(label = "setspawn", permission = "technique.setspawn")
public class SetSpawnCommand {

    public void execute(Player player) {

        TechniquePlugin.get().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.GREEN + "yo sup homie? did you try to set a spawn? nice, i got you!");
    }

}
