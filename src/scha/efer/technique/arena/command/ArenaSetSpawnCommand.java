package scha.efer.technique.arena.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena setspawn", permission = "technique.dev")
public class ArenaSetSpawnCommand {

    public void execute(Player player, Arena arena, String pos) {
        if (arena != null) {
            if (pos.equalsIgnoreCase("a")) {
                arena.setSpawn1(player.getLocation());
            } else if (pos.equalsIgnoreCase("b")) {
                arena.setSpawn2(player.getLocation());
            } else {
                player.sendMessage(CC.RED + "Invalid spawn point. Try \"a\" or \"b\".");
                return;
            }

            arena.save();

            player.sendMessage(CC.GOLD + "Updated spawn point \"" + pos + "\" for arena \"" + arena.getName() + "\"");
        } else {
            player.sendMessage(CC.RED + "An arena with that name already exists.");
        }
    }

}