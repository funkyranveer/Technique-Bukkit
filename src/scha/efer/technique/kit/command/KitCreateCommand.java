package scha.efer.technique.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit create", permission = "technique.kit.create")
public class KitCreateCommand {

    public void execute(Player player, String kitName) {
        if (Kit.getByName(kitName) != null) {
            player.sendMessage(CC.RED + "A kit with that name already exists.");
            return;
        }

        Kit kit = new Kit(kitName);
        kit.save();

        Kit.getKits().add(kit);

        player.sendMessage(CC.GREEN + "You created a new kit.");
    }

}
