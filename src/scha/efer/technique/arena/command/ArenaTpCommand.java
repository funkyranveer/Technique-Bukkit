package scha.efer.technique.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.arena.Arena;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena tp", permission = "technique.admin.arena")
public class ArenaTpCommand {

    public void execute(Player player, @CPL("Arena") Arena arena) {
        if (arena != null) {
            player.teleport(arena.getSpawn1());
        }
    }

}
