package scha.efer.technique.event.impl.brackets;

import scha.efer.technique.TechniquePlugin;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class BracketsTask extends BukkitRunnable {

    private int ticks;
    private final Brackets brackets;
    private final BracketsState eventState;

    public BracketsTask(Brackets brackets, BracketsState eventState) {
        this.brackets = brackets;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (TechniquePlugin.get().getBracketsManager().getActiveBrackets() == null ||
                !TechniquePlugin.get().getBracketsManager().getActiveBrackets().equals(brackets) || brackets.getState() != eventState) {
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
