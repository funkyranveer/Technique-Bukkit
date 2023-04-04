package scha.efer.technique.util.essentials;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.TaskUtil;
import scha.efer.technique.util.bootstrap.Bootstrapped;
import scha.efer.technique.util.essentials.event.SpawnTeleportEvent;
import scha.efer.technique.util.external.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Essentials extends Bootstrapped {

    private static Location spawn;

    public Essentials(TechniquePlugin Practice) {
        super(Practice);

        spawn = LocationUtil.deserialize(Practice.getMainConfig().getStringOrDefault("ESSENTIAL.SPAWN_LOCATION", null));
    }

    public void setSpawn(Location location) {
        spawn = location;

        Practice.getMainConfig().getConfiguration().set("ESSENTIAL.SPAWN_LOCATION", LocationUtil.serialize(this.spawn));

        try {
            Practice.getMainConfig().getConfiguration().save(Practice.getMainConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void teleportToSpawn(Player player) {
        Location location = spawn;

        SpawnTeleportEvent event = new SpawnTeleportEvent(player, location);
        event.call();

        if (!event.isCancelled() && event.getLocation() != null) {
            TaskUtil.runAsync(() -> player.teleport(event.getLocation()));
        }
    }

    public void teleportToPackShowcase(Player player) {
        TaskUtil.runAsync(() -> player.teleport(new Location(player.getLocation().getWorld(), 0.500, 75, 77.500, -180, 0.8F)));
    }

    public int clearEntities(World world) {
        int removed = 0;

        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.PLAYER || entity.getType() == EntityType.ITEM_FRAME) {
                continue;
            }

            removed++;
            entity.remove();
        }

        return removed;
    }

    public int clearEntities(World world, EntityType... excluded) {
        int removed = 0;

        entityLoop:
        for (Entity entity : world.getEntities()) {
            for (EntityType type : excluded) {
                if (entity.getType() == EntityType.PLAYER) {
                    continue entityLoop;
                }

                if (entity.getType() == type) {
                    continue entityLoop;
                }
            }

            removed++;
            entity.remove();
        }

        return removed;
    }

}
