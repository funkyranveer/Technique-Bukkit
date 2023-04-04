package scha.efer.technique.event.impl.spleef.task;

import scha.efer.technique.event.impl.spleef.Spleef;
import scha.efer.technique.event.impl.spleef.SpleefState;
import scha.efer.technique.event.impl.spleef.SpleefTask;
import scha.efer.technique.util.external.CC;

public class SpleefRoundStartTask extends SpleefTask {

    public SpleefRoundStartTask(Spleef spleef) {
        super(spleef, SpleefState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getSpleef().broadcastMessage(CC.GREEN + "The round has started!");
            this.getSpleef().setEventTask(null);
            this.getSpleef().setState(SpleefState.ROUND_FIGHTING);

            this.getSpleef().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getSpleef().broadcastMessage("&eThe round will start in &d" + seconds + "&e seconds.");
        }
    }

}
