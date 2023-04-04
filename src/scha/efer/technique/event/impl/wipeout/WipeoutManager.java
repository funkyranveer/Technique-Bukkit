package scha.efer.technique.event.impl.wipeout;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.wipeout.task.WipeoutStartTask;
import scha.efer.technique.util.external.Cooldown;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class WipeoutManager {

    @Getter
    private Wipeout activeWipeout;
    @Getter
    @Setter
    private Cooldown cooldown = new Cooldown(0);
    @Getter
    @Setter
    private Location wipeoutSpawn;

    public WipeoutManager() {
        load();
    }

    public void setActiveWipeout(Wipeout wipeout) {
        if (activeWipeout != null) {
            activeWipeout.setEventTask(null);
        }

        if (wipeout == null) {
            activeWipeout = null;
            return;
        }

        activeWipeout = wipeout;
        activeWipeout.setEventTask(new WipeoutStartTask(wipeout));
    }

    public void load() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (configuration.contains("events.wipeout.spectator")) {
            wipeoutSpawn = LocationUtil.deserialize(configuration.getString("events.wipeout.spectator"));
        }
    }

    public void save() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (wipeoutSpawn != null) {
            configuration.set("events.wipeout.spectator", LocationUtil.serialize(wipeoutSpawn));
        }

        try {
            configuration.save(TechniquePlugin.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
