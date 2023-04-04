package scha.efer.technique.event.impl.juggernaut;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.juggernaut.task.JuggernautStartTask;
import scha.efer.technique.util.external.Cooldown;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class JuggernautManager {

    @Getter
    private Juggernaut activeJuggernaut;
    @Getter
    @Setter
    private Cooldown cooldown = new Cooldown(0);
    @Getter
    @Setter
    private Location juggernautSpectator;
    @Getter
    @Setter
    private String juggernautKnockbackProfile;

    public JuggernautManager() {
        load();
    }

    public void setActiveJuggernaut(Juggernaut juggernaut) {
        if (activeJuggernaut != null) {
            activeJuggernaut.setEventTask(null);
        }

        if (juggernaut == null) {
            activeJuggernaut = null;
            return;
        }

        activeJuggernaut = juggernaut;
        activeJuggernaut.setEventTask(new JuggernautStartTask(juggernaut));
    }

    public void load() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (configuration.contains("events.juggernaut.spectator")) {
            juggernautSpectator = LocationUtil.deserialize(configuration.getString("events.juggernaut.spectator"));
        }

        if (configuration.contains("events.juggernaut.knockback-profile")) {
            juggernautKnockbackProfile = configuration.getString("events.juggernaut.knockback-profile");
        }
    }

    public void save() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (juggernautSpectator != null) {
            configuration.set("events.juggernaut.spectator", LocationUtil.serialize(juggernautSpectator));
        }

        if (juggernautKnockbackProfile != null) {
            configuration.set("events.juggernaut.knockback-profile", juggernautKnockbackProfile);
        }

        try {
            configuration.save(TechniquePlugin.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
