package scha.efer.technique.event.impl.infected;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.infected.task.InfectedStartTask;
import scha.efer.technique.util.external.Cooldown;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class InfectedManager {

    @Getter
    private Infected activeInfected;
    @Getter
    @Setter
    private Cooldown cooldown = new Cooldown(0);
    @Getter
    @Setter
    private Location infectedSpawn1;
    @Getter
    @Setter
    private Location infectedSpawn2;
    @Getter
    @Setter
    private String infectedKnockbackProfile;

    public InfectedManager() {
        load();
    }

    public void setActiveInfected(Infected infected) {
        if (activeInfected != null) {
            activeInfected.setEventTask(null);
        }

        if (infected == null) {
            activeInfected = null;
            return;
        }

        activeInfected = infected;
        activeInfected.setEventTask(new InfectedStartTask(infected));
    }

    public void load() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (configuration.contains("events.infected.spawn1")) {
            infectedSpawn1 = LocationUtil.deserialize(configuration.getString("events.infected.spawn1"));
        }

        if (configuration.contains("events.infected.spawn2")) {
            infectedSpawn2 = LocationUtil.deserialize(configuration.getString("events.infected.spawn2"));
        }

        if (configuration.contains("events.infected.knockback-profile")) {
            infectedKnockbackProfile = configuration.getString("events.infected.knockback-profile");
        }
    }

    public void save() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (infectedSpawn1 != null) {
            configuration.set("events.infected.spawn1", LocationUtil.serialize(infectedSpawn1));
        }

        if (infectedSpawn2 != null) {
            configuration.set("events.infected.spawn2", LocationUtil.serialize(infectedSpawn2));
        }

        if (infectedKnockbackProfile != null) {
            configuration.set("events.infected.knockback-profile", infectedKnockbackProfile);
        }

        try {
            configuration.save(TechniquePlugin.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
