package scha.efer.technique.event.impl.spleef.task;

import scha.efer.technique.event.impl.spleef.Spleef;
import scha.efer.technique.event.impl.spleef.SpleefState;
import scha.efer.technique.event.impl.spleef.SpleefTask;

public class SpleefRoundEndTask extends SpleefTask {

    public SpleefRoundEndTask(Spleef spleef) {
        super(spleef, SpleefState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            if (this.getSpleef().canEnd()) {
                this.getSpleef().end();
            }
        }
    }

}
