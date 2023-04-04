package scha.efer.technique.event.impl.infected;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class InfectedTask extends BukkitRunnable {

    private int ticks;
    private final Infected infected;
    private final InfectedState eventState;

    public InfectedTask(Infected infected, InfectedState eventState) {
        this.infected = infected;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (TechniquePlugin.get().getInfectedManager().getActiveInfected() == null ||
                !TechniquePlugin.get().getInfectedManager().getActiveInfected().equals(infected) || infected.getState() != eventState) {
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
