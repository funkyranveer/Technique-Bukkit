package scha.efer.technique.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@CommandMeta(label = "kit setloadout", permission = "technique.kit.setloadout")
public class KitSetLoadoutCommand {

    public void execute(Player player, Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        kit.getKitLoadout().setArmor(player.getInventory().getArmorContents());
        kit.getKitLoadout().setContents(player.getInventory().getContents());
        List<PotionEffect> potionEffects = new ArrayList<>();
        potionEffects.addAll(player.getActivePotionEffects());
        kit.getKitLoadout().setEffects(potionEffects);
        kit.save();

        player.sendMessage(CC.GREEN + "You updated the kit's loadout.");
    }

}
