package scha.efer.technique.event.impl.infected.task;

import scha.efer.technique.event.impl.infected.Infected;
import scha.efer.technique.event.impl.infected.InfectedState;
import scha.efer.technique.event.impl.infected.InfectedTask;

public class InfectedFightingTask extends InfectedTask {

    public InfectedFightingTask(Infected infected) {
        super(infected, InfectedState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 600) {
            if (!getInfected().canEnd().equalsIgnoreCase("None") && getInfected().getState().equals(InfectedState.ROUND_ENDING)) {
                getInfected().setState(InfectedState.ROUND_ENDING);
                getInfected().setEventTask(new InfectedRoundEndTask(getInfected()));
            }
        }
    }

}
