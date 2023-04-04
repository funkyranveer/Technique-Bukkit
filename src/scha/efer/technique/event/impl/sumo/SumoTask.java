package scha.efer.technique.event.impl.sumo;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class SumoTask extends BukkitRunnable {

    private int ticks;
    private final Sumo sumo;
    private final SumoState eventState;

    public SumoTask(Sumo sumo, SumoState eventState) {
        this.sumo = sumo;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (TechniquePlugin.get().getSumoManager().getActiveSumo() == null ||
                !TechniquePlugin.get().getSumoManager().getActiveSumo().equals(sumo) || sumo.getState() != eventState) {
            cancel();
            return;
        }

        onRun();

        ticks++;
    }

    public int getSeconds() {
        return 3 - ticks;
    }

    public abstract void onRun();

}
