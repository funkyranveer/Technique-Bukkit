package scha.efer.technique.event.impl.spleef;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class SpleefTask extends BukkitRunnable {

    private int ticks;
    private final Spleef spleef;
    private final SpleefState eventState;

    public SpleefTask(Spleef spleef, SpleefState eventState) {
        this.spleef = spleef;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (TechniquePlugin.get().getSpleefManager().getActiveSpleef() == null ||
                !TechniquePlugin.get().getSpleefManager().getActiveSpleef().equals(spleef) || spleef.getState() != eventState) {
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
