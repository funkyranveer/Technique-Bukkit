package scha.efer.technique.arena.command;

import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.util.external.CC;

import java.io.IOException;

@CommandMeta(label = "arena setbed", permission = "technique.dev")
public class ArenaSetBedCommand {

    public void execute(Player player, Arena arena, String pos) {
        FileConfiguration config = TechniquePlugin.get().getArenasConfig().getConfiguration();
        if (arena != null) {
            if (pos.equalsIgnoreCase("a")) {
                arena.setBedLocationA(player.getLocation());
                config.set("arenas." + arena.getName() + ".red_bed.x", player.getLocation().getBlockX());
                config.set("arenas." + arena.getName() + ".red_bed.y", player.getLocation().getBlockY());
                config.set("arenas." + arena.getName() + ".red_bed.z", player.getLocation().getBlockZ());
                try {
                    config.save(TechniquePlugin.get().getArenasConfig().getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.sendMessage(CC.translate("&aYou have successfully set &cRed&a's bed!"));
            } else if (pos.equalsIgnoreCase("b")) {
                arena.setBedLocationB(player.getLocation());
                config.set("arenas." + arena.getName() + ".blue_bed.x", player.getLocation().getBlockX());
                config.set("arenas." + arena.getName() + ".blue_bed.y", player.getLocation().getBlockY());
                config.set("arenas." + arena.getName() + ".blue_bed.z", player.getLocation().getBlockZ());

                try {
                    config.save(TechniquePlugin.get().getArenasConfig().getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.sendMessage(CC.translate("&aYou have successfully set &9Blue&a's bed!"));
            } else {
                player.sendMessage(CC.RED + "Invalid spawn point. Try \"a\" or \"b\".");
                return;
            }

            arena.save();
            player.sendMessage(CC.GREEN + "Saving arena config...");
            player.sendMessage(CC.YELLOW + "Done!");
        } else {
            player.sendMessage(CC.RED + "An arena with that name already exists.");
        }
    }

}