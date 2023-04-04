package scha.efer.technique.event.impl.wipeout;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class WipeoutTask extends BukkitRunnable {

    private int ticks;
    private final Wipeout wipeout;
    private final WipeoutState eventState;

    public WipeoutTask(Wipeout wipeout, WipeoutState eventState) {
        this.wipeout = wipeout;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (TechniquePlugin.get().getWipeoutManager().getActiveWipeout() == null ||
                !TechniquePlugin.get().getWipeoutManager().getActiveWipeout().equals(wipeout) || wipeout.getState() != eventState) {
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
