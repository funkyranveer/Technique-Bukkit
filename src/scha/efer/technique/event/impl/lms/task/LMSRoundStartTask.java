package scha.efer.technique.event.impl.lms.task;

import scha.efer.technique.event.impl.lms.LMS;
import scha.efer.technique.event.impl.lms.LMSState;
import scha.efer.technique.event.impl.lms.LMSTask;
import scha.efer.technique.util.external.CC;

public class LMSRoundStartTask extends LMSTask {

    public LMSRoundStartTask(LMS LMS) {
        super(LMS, LMSState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getLMS().broadcastMessage(CC.GREEN + "The round has started!");
            this.getLMS().setEventTask(null);
            this.getLMS().setState(LMSState.ROUND_FIGHTING);

            this.getLMS().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getLMS().broadcastMessage("&eThe round will start in &d" + seconds + "&e seconds.");
        }
    }

}
