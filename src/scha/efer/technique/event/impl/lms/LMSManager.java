package scha.efer.technique.event.impl.lms;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.lms.task.LMSStartTask;
import scha.efer.technique.util.external.Cooldown;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class LMSManager {

    @Getter
    private LMS activeLMS;
    @Getter
    @Setter
    private Cooldown cooldown = new Cooldown(0);
    @Getter
    @Setter
    private Location lmsSpectator;
    @Getter
    @Setter
    private String lmsKnockbackProfile;

    public LMSManager() {
        load();
    }

    public void setActiveLMS(LMS LMS) {
        if (activeLMS != null) {
            activeLMS.setEventTask(null);
        }

        if (LMS == null) {
            activeLMS = null;
            return;
        }

        activeLMS = LMS;
        activeLMS.setEventTask(new LMSStartTask(LMS));
    }

    public void load() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (configuration.contains("events.ffa.spectator")) {
            lmsSpectator = LocationUtil.deserialize(configuration.getString("events.ffa.spectator"));
        }

        if (configuration.contains("events.ffa.knockback-profile")) {
            lmsKnockbackProfile = configuration.getString("events.ffa.knockback-profile");
        }
    }

    public void save() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (lmsSpectator != null) {
            configuration.set("events.ffa.spectator", LocationUtil.serialize(lmsSpectator));
        }

        if (lmsKnockbackProfile != null) {
            configuration.set("events.ffa.knockback-profile", lmsKnockbackProfile);
        }

        try {
            configuration.save(TechniquePlugin.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
