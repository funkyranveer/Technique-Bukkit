package scha.efer.technique.event.impl.juggernaut;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class JuggernautTask extends BukkitRunnable {

    private int ticks;
    private final Juggernaut juggernaut;
    private final JuggernautState eventState;

    public JuggernautTask(Juggernaut juggernaut, JuggernautState eventState) {
        this.juggernaut = juggernaut;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut() == null ||
                !TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().equals(juggernaut) || juggernaut.getState() != eventState) {
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
