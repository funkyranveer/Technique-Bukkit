package scha.efer.technique.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit setkb", permission = "technique.kit.setkb")
public class KitSetKnockbackProfileCommand {

    public void execute(Player player, Kit kit, @CPL("KnockbackProfile") String knockbackProfile) {
        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        kit.setKnockbackProfile(knockbackProfile);
        kit.save();

        player.sendMessage(CC.GREEN + "You updated the kit's knockbackprofile to" + knockbackProfile);
    }

}
