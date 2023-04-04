package scha.efer.technique.event.impl.infected.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "infected setspawn", permission = "technique.infected.setspawn")
public class InfectedSetSpawnCommand {

    public void execute(Player player, @CPL("one/two") String position) {
        if (!(position.equals("one") || position.equals("two"))) {
            player.sendMessage(CC.RED + "The position must be one/two.");
        } else {
            if (position.equals("one")) {
                TechniquePlugin.get().getInfectedManager().setInfectedSpawn1(player.getLocation());
            } else if (position.equals("two")) {
                TechniquePlugin.get().getInfectedManager().setInfectedSpawn2(player.getLocation());
            }

            player.sendMessage(CC.GREEN + "Updated infected's spawn location " + position + ".");

            TechniquePlugin.get().getInfectedManager().save();
        }
    }

}
