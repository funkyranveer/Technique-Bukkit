package scha.efer.technique.event.impl.brackets.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets setspawn", permission = "technique.brackets.setspawn")
public class BracketsSetSpawnCommand {

    public void execute(Player player, @CPL("one/two/spec") String position) {
        if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
            player.sendMessage(CC.RED + "The position must be one/two/spec.");
        } else {
            if (position.equals("one")) {
                TechniquePlugin.get().getBracketsManager().setBracketsSpawn1(player.getLocation());
            } else if (position.equals("two")) {
                TechniquePlugin.get().getBracketsManager().setBracketsSpawn2(player.getLocation());
            } else {
                TechniquePlugin.get().getBracketsManager().setBracketsSpectator(player.getLocation());
            }

            player.sendMessage(CC.GREEN + "Updated brackets's spawn location " + position + ".");

            TechniquePlugin.get().getBracketsManager().save();
        }
    }

}
