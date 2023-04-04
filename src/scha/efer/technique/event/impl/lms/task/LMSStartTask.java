package scha.efer.technique.event.impl.lms.task;

import scha.efer.technique.event.impl.lms.LMS;
import scha.efer.technique.event.impl.lms.LMSState;
import scha.efer.technique.event.impl.lms.LMSTask;
import scha.efer.technique.util.external.Cooldown;

public class LMSStartTask extends LMSTask {

    public LMSStartTask(LMS LMS) {
        super(LMS, LMSState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getLMS().end();
            return;
        }

        if (this.getLMS().getPlayers().size() <= 1 && this.getLMS().getCooldown() != null) {
            this.getLMS().setCooldown(null);
            this.getLMS().broadcastMessage("&5There are not enough players for the ffa to start.");
        }

        if (this.getLMS().getPlayers().size() == this.getLMS().getMaxPlayers() || (getTicks() >= 30 && this.getLMS().getPlayers().size() >= 2)) {
            if (this.getLMS().getCooldown() == null) {
                this.getLMS().setCooldown(new Cooldown(11_000));
                this.getLMS().broadcastMessage("&fThe LMS will start in &510 seconds&f...");
            } else {
                if (this.getLMS().getCooldown().hasExpired()) {
                    this.getLMS().setState(LMSState.ROUND_STARTING);
                    this.getLMS().onRound();
                    this.getLMS().setTotalPlayers(this.getLMS().getPlayers().size());
                    this.getLMS().setEventTask(new LMSRoundStartTask(this.getLMS()));
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getLMS().announce();
        }
    }

}
