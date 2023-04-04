package scha.efer.technique.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena addkit", permission = "technique.admin.arena")
public class ArenaAddKitCommand {

    public void execute(Player player, @CPL("arena") Arena arena, @CPL("kit") Kit kit) {
        if (arena == null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Arena does not exist");
            return;
        }

        if (kit == null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Kit does not exist");
            return;
        }

        if (!arena.getKits().contains(kit.getName()))
            arena.getKits().add(kit.getName());

        player.sendMessage(ChatColor.GREEN + "Successfully added the kit " + kit.getName() + " to " + arena.getName());
        arena.save();
    }

}