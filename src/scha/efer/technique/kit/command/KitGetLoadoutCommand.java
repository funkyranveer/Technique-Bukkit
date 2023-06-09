package scha.efer.technique.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit getloadout", permission = "technique.kit.getloadout")
public class KitGetLoadoutCommand {

    public void execute(Player player, Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
        player.getInventory().setContents(kit.getKitLoadout().getContents());
        player.addPotionEffects(kit.getKitLoadout().getEffects());
        player.updateInventory();

        player.sendMessage(CC.GREEN + "You received the kit's loadout.");
    }

}
