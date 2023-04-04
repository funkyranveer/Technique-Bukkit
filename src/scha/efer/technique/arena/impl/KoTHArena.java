package scha.efer.technique.arena.impl;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.ArenaType;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter
@Setter
public class KoTHArena extends Arena {

    public KoTHArena(String name) {
        super(name);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.KOTH;
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

        if (point1 != null) {
            configuration.set(path + ".point1", LocationUtil.serialize(point1));
        }

        if (point2 != null) {
            configuration.set(path + ".point2", LocationUtil.serialize(point2));
        }

        configuration.set(path + ".kits", getKits());

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
