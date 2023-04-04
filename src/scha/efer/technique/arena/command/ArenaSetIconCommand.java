package scha.efer.technique.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.util.external.CC;

@CommandMeta(label={"arena seticon"}, permission="technique.dev")
public class ArenaSetIconCommand {
    public void execute(Player player, @CPL("arena") Arena arena) {
    ItemStack item = player.getItemInHand();
    if (item == null) {
        player.sendMessage(CC.translate("&cPlease hold a valid item in your hand."));
    }
    else if (arena == null) {
        player.sendMessage(CC.translate("&cAn arena with that name does not exist."));
    } else {
        arena.setDisplayIcon(item);
        arena.save();
        player.sendMessage(CC.translate("&aSuccessfully set the arena icon to the item in your hand."));
    }
  }
}
