package scha.efer.technique.event.impl.skywars.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.impl.skywars.ChestType;
import scha.efer.technique.event.impl.skywars.SkyWarsChest;
import scha.efer.technique.util.external.CC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

@CommandMeta(label = "skywars setchest", permission = "technique.skywars.setchest")
public class SkyWarsSetChestCommand {

    public void execute(Player player, @CPL(value = "type") String stringType) {
        try {
            ChestType.valueOf(stringType);
        } catch (Exception e) {
            player.sendMessage(CC.translate("&5Invalid chest type."));
            return;
        }
        ChestType type = ChestType.valueOf(stringType);
        Location playerLookingAt = player.getTargetBlock((Set<Material>) null, 100).getLocation();

        if (SkyWarsChest.getFromLocation(playerLookingAt) != null) {
            SkyWarsChest chest = SkyWarsChest.getFromLocation(playerLookingAt);
            chest.setType(type);
            chest.save();
            player.sendMessage(CC.translate("&aSuccessfully set existing chest at current location to type " + type.name()));
        } else {
            SkyWarsChest chest = new SkyWarsChest(UUID.randomUUID(), playerLookingAt, type);
            chest.save();
            player.sendMessage(CC.translate("&aSuccessfully saved new chest on type " + type.name()));
        }

    }

}
