package scha.efer.technique.arena.impl;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.ArenaType;
import scha.efer.technique.cuboid.Cuboid;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TheBridgeArena extends Arena {

    private final List<Arena> duplicates = new ArrayList<>();
    @Setter private Cuboid redCuboid;
    @Setter private Cuboid blueCuboid;

    public TheBridgeArena(String name) {
        super(name);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.THEBRIDGE;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        FileConfiguration configuration = TechniquePlugin.get().getArenasConfig().getConfiguration();
        configuration.set(path, null);
        configuration.set(path + ".type", getType().name());
        /*configuration.set(path + ".icon.material", displayIcon.getType().name());
        configuration.set(path + ".icon.durability", displayIcon.getDurability());
        configuration.set(path + ".disable-pearls", disablePearls);*/

        if (spawn1 != null) {
            configuration.set(path + ".spawn1", LocationUtil.serialize(spawn1));
        }

        if (spawn2 != null) {
            configuration.set(path + ".spawn2", LocationUtil.serialize(spawn2));
        }

        configuration.set(path + ".kits", getKits());

        if(redCuboid != null){
            configuration.set(path + ".redCuboid.location1", LocationUtil.serialize(redCuboid.getLowerCorner()));
            configuration.set(path + ".redCuboid.location2", LocationUtil.serialize(redCuboid.getUpperCorner()));
        }
        if(blueCuboid != null){
            configuration.set(path + ".blueCuboid.location1", LocationUtil.serialize(blueCuboid.getLowerCorner()));
            configuration.set(path + ".blueCuboid.location2", LocationUtil.serialize(blueCuboid.getUpperCorner()));
        }

        if (!duplicates.isEmpty()) {
            int i = 0;

            for (Arena duplicate : duplicates) {
                i++;

                configuration.set(path + ".duplicates." + i + ".spawn1", LocationUtil.serialize(duplicate.getSpawn1()));
                configuration.set(path + ".duplicates." + i + ".spawn2", LocationUtil.serialize(duplicate.getSpawn2()));
            }
        }

        try {
            configuration.save(TechniquePlugin.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() {
        FileConfiguration configuration = TechniquePlugin.get().getArenasConfig().getConfiguration();
        configuration.set("arenas." + getName(), null);

        try {
            configuration.save(TechniquePlugin.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
