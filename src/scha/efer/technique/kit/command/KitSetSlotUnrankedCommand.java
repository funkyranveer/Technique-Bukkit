package scha.efer.technique.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.CC;

@CommandMeta(label = "kit setslotunranked", permission = "technique.kit.setkb")
public class KitSetSlotUnrankedCommand {

    public void execute(Player player, Kit kit, Integer slot) {
        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        kit.setUnrankedSlot(slot);
        kit.save();

        player.sendMessage(CC.GREEN + "You updated the kit's unranked slot to " + slot + ".");
    }

}
