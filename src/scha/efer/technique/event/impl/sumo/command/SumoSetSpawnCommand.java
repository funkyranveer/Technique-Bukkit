package scha.efer.technique.event.impl.sumo.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo setspawn", permission = "technique.sumo.setspawn")
public class SumoSetSpawnCommand {

    public void execute(Player player, @CPL("one/two/spec") String position) {
        if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
            player.sendMessage(CC.RED + "The position must be 1 or 2.");
        } else {
            if (position.equals("one")) {
                TechniquePlugin.get().getSumoManager().setSumoSpawn1(player.getLocation());
            } else if (position.equals("two")) {
                TechniquePlugin.get().getSumoManager().setSumoSpawn2(player.getLocation());
            } else {
                TechniquePlugin.get().getSumoManager().setSumoSpectator(player.getLocation());
            }

            player.sendMessage(CC.GREEN + "Updated sumo's spawn location " + position + ".");

            TechniquePlugin.get().getSumoManager().save();
        }
    }

}
