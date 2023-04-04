package scha.efer.technique.event.impl.lms.task;

import scha.efer.technique.event.impl.lms.LMS;
import scha.efer.technique.event.impl.lms.LMSState;
import scha.efer.technique.event.impl.lms.LMSTask;

public class LMSRoundEndTask extends LMSTask {

    public LMSRoundEndTask(LMS LMS) {
        super(LMS, LMSState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            if (this.getLMS().canEnd()) {
                this.getLMS().end();
            }
        }
    }

}
