package scha.efer.technique.event.impl.skywars;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class SkyWarsChest {

    private UUID uuid;
    private Location location;
    private Chest block;
    private ChestType type;

    public static Map<Location, SkyWarsChest> chests = new HashMap<>();

    private static YamlConfiguration config = TechniquePlugin.get().getChestsConfig().getConfiguration();

    public SkyWarsChest(@NotNull UUID uuid, @NotNull Location location, @NotNull ChestType type) {
        this.uuid = uuid;
        this.location = location;
        this.block = (Chest) location.getBlock().getState();
        this.type = type;
        chests.put(location, this);
    }

    public void save() {
        config.set(uuid.toString(), location.getWorld().getName() + "@" + location.getX() + "@" + location.getY() + "@" + location.getZ() + "#" + type.name());
        try {
            config.save(TechniquePlugin.get().getChestsConfig().getFile());
        } catch (Exception ignored) {}
    }

    public static void loadAll() {
        for (String uuidString : config.getKeys(false)) {
            String[] args = config.getString(uuidString).split("#");
            String[] locArgs = args[0].split("@");
            new SkyWarsChest(UUID.fromString(uuidString),
                    new Location(Bukkit.getWorld(locArgs[0]), Double.parseDouble(locArgs[1]), Double.parseDouble(locArgs[2]), Double.parseDouble(locArgs[3])),
                    ChestType.valueOf(args[1]));
        }
    }

    public static SkyWarsChest getFromLocation(Location location) {
        return chests.getOrDefault(location, null);
    }

}
