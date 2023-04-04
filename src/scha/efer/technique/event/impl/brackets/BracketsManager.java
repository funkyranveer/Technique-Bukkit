package scha.efer.technique.event.impl.brackets;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.brackets.task.BracketsStartTask;
import scha.efer.technique.util.external.Cooldown;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class BracketsManager {

    @Getter
    private Brackets activeBrackets;
    @Getter
    @Setter
    private Cooldown cooldown = new Cooldown(0);
    @Getter
    @Setter
    private Location bracketsSpectator;
    @Getter
    @Setter
    private Location bracketsSpawn1;
    @Getter
    @Setter
    private Location bracketsSpawn2;
    @Getter
    @Setter
    private String bracketsKnockbackProfile;

    public BracketsManager() {
        load();
    }

    public void setActiveBrackets(Brackets brackets) {
        if (activeBrackets != null) {
            activeBrackets.setEventTask(null);
        }

        if (brackets == null) {
            activeBrackets = null;
            return;
        }

        activeBrackets = brackets;
        activeBrackets.setEventTask(new BracketsStartTask(brackets));
    }

    public void load() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (configuration.contains("events.brackets.spectator")) {
            bracketsSpectator = LocationUtil.deserialize(configuration.getString("events.brackets.spectator"));
        }

        if (configuration.contains("events.brackets.spawn1")) {
            bracketsSpawn1 = LocationUtil.deserialize(configuration.getString("events.brackets.spawn1"));
        }

        if (configuration.contains("events.brackets.spawn2")) {
            bracketsSpawn2 = LocationUtil.deserialize(configuration.getString("events.brackets.spawn2"));
        }

        if (configuration.contains("events.brackets.knockback-profile")) {
            bracketsKnockbackProfile = configuration.getString("events.brackets.knockback-profile");
        }
    }

    public void save() {
        FileConfiguration configuration = TechniquePlugin.get().getEventsConfig().getConfiguration();

        if (bracketsSpectator != null) {
            configuration.set("events.brackets.spectator", LocationUtil.serialize(bracketsSpectator));
        }

        if (bracketsSpawn1 != null) {
            configuration.set("events.brackets.spawn1", LocationUtil.serialize(bracketsSpawn1));
        }

        if (bracketsSpawn2 != null) {
            configuration.set("events.brackets.spawn2", LocationUtil.serialize(bracketsSpawn2));
        }

        if (bracketsKnockbackProfile != null) {
            configuration.set("events.brackets.knockback-profile", bracketsKnockbackProfile);
        }

        try {
            configuration.save(TechniquePlugin.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
