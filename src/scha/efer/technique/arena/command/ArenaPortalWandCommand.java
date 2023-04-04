package scha.efer.technique.arena.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.arena.selection.Selection;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"arena wand", "arena portalwand", "arena selection", "arena portal"}, permission = "technique.dev")
public class ArenaPortalWandCommand {
    public void execute(Player player) {
        if (player.getInventory().first(Selection.SELECTION_WAND) != -1) {
            player.getInventory().remove(Selection.SELECTION_WAND);
        } else {
            player.getInventory().addItem(Selection.SELECTION_WAND);
            player.sendMessage(CC.translate("&8[&5TIP&8] &7&oLeft-Click to select first position and Right-Click to select second position."));
        }

        player.updateInventory();
    }
}
