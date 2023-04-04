package scha.efer.technique.match.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.match.MatchSnapshot;
import scha.efer.technique.match.menu.MatchDetailsMenu;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandMeta(label = "viewinv")
public class ViewInventoryCommand {

    public void execute(Player player, String id) {
        MatchSnapshot cachedInventory;

        try {
            cachedInventory = MatchSnapshot.getByUuid(UUID.fromString(id));
        } catch (Exception e) {
            cachedInventory = MatchSnapshot.getByName(id);
        }

        if (cachedInventory == null) {
            player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
            return;
        }

        new MatchDetailsMenu(cachedInventory, null).openMenu(player);
    }

}
