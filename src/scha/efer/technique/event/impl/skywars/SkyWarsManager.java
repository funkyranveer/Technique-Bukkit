package scha.efer.technique.event.impl.skywars;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.skywars.task.SkyWarsStartTask;
import scha.efer.technique.util.external.Cooldown;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkyWarsManager {

    @Getter
    private SkyWars activeSkyWars;
    @Getter
    @Setter
    private Cooldown cooldown = new Cooldown(0);
    @Getter
    @Setter
    private List<String> skyWarsSpectators = new ArrayList<>();
    @Getter
    @Setter
    private String skyWarsKnockbackProfile;

    public SkyWarsManager() {
        load();
    }

    public void setActiveSkyWars(SkyWars skyWars) {
        if (activeSkyWars != null) {
            activeSkyWars.setEventTask(null);
        }

        if (skyWars == null) {
            activeSkyWars = null;
            return;
        }

        activeSkyWars = skyWars;
        activeSkyWars.setEventTask(new SkyWarsStartTask(skyWars));
    }

    public void load() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (configuration.contains("events.skywars.spectator")) {
            for (String loc : configuration.getStringList("events.skywars.spectator")) {
                this.getSkyWarsSpectators().add(loc);
            }
        }

        if (configuration.contains("events.skyWars.knockback-profile")) {
            skyWarsKnockbackProfile = configuration.getString("events.skyWars.knockback-profile");
        }
    }

    public void save() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (!skyWarsSpectators.isEmpty()) {
            configuration.set("events.skywars.spectator", this.skyWarsSpectators);
        }

        if (skyWarsKnockbackProfile != null) {
            configuration.set("events.skywars.knockback-profile", skyWarsKnockbackProfile);
        }

        try {
            configuration.save(TechniquePlugin.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
