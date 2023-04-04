package scha.efer.technique.event.impl.parkour;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class ParkourTask extends BukkitRunnable {

    private int ticks;
    private final Parkour parkour;
    private final ParkourState eventState;

    public ParkourTask(Parkour parkour, ParkourState eventState) {
        this.parkour = parkour;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (TechniquePlugin.get().getParkourManager().getActiveParkour() == null ||
                !TechniquePlugin.get().getParkourManager().getActiveParkour().equals(parkour) || parkour.getState() != eventState) {
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
