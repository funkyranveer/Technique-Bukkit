package scha.efer.technique.event.impl.sumo.task;

import scha.efer.technique.event.impl.sumo.Sumo;
import scha.efer.technique.event.impl.sumo.SumoState;
import scha.efer.technique.event.impl.sumo.SumoTask;

public class SumoRoundEndTask extends SumoTask {

    public SumoRoundEndTask(Sumo sumo) {
        super(sumo, SumoState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (this.getSumo().canEnd()) {
            this.getSumo().end();
        } else {
            if (getTicks() >= 3) {
                this.getSumo().onRound();
            }
        }
    }

}
