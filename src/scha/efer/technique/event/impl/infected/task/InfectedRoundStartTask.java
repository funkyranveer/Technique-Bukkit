package scha.efer.technique.event.impl.infected.task;

import scha.efer.technique.event.impl.infected.Infected;
import scha.efer.technique.event.impl.infected.InfectedState;
import scha.efer.technique.event.impl.infected.InfectedTask;
import scha.efer.technique.util.external.CC;

public class InfectedRoundStartTask extends InfectedTask {

    public InfectedRoundStartTask(Infected infected) {
        super(infected, InfectedState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getInfected().broadcastMessage(CC.RED + "The event has started! (Survive 10 minutes to win)");
            this.getInfected().setEventTask(new InfectedFightingTask(getInfected()));
            this.getInfected().setState(InfectedState.ROUND_FIGHTING);
            this.getInfected().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getInfected().broadcastMessage("&eThe round will start in &d" + seconds + "&e seconds.");
        }
    }

}
