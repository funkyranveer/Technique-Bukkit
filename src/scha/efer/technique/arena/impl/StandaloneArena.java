package scha.efer.technique.arena.impl;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.ArenaType;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class StandaloneArena extends Arena {

    private final List<Arena> duplicates = new ArrayList<>();

    public StandaloneArena(String name) {
        super(name);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.STANDALONE;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        FileConfiguration configuration = TechniquePlugin.get().getArenasConfig().getConfiguration();
        configuration.set(path, null);
        configuration.set(path + ".type", getType().name());

        if (spawn1 != null) {
            configuration.set(path + ".spawn1", LocationUtil.serialize(spawn1));
        }

        if (spawn2 != null) {
            configuration.set(path + ".spawn2", LocationUtil.serialize(spawn2));
        }

        if (bedLocationA != null) {
            configuration.set(path + ".red_bed.coord", LocationUtil.serialize(bedLocationA));
        }

        if (bedLocationB != null) {
            configuration.set(path + ".blue_bed.coord", LocationUtil.serialize(bedLocationB));
        }

        configuration.set(path + ".kits", getKits());

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
