package scha.efer.technique.event.impl.wipeout.task;

import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.event.impl.wipeout.WipeoutState;
import scha.efer.technique.event.impl.wipeout.WipeoutTask;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.Cooldown;

public class WipeoutStartTask extends WipeoutTask {

    public WipeoutStartTask(Wipeout wipeout) {
        super(wipeout, WipeoutState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getWipeout().end(null);
            return;
        }

        if (this.getWipeout().getPlayers().size() <= 1 && this.getWipeout().getCooldown() != null) {
            this.getWipeout().setCooldown(null);
            this.getWipeout().broadcastMessage("&5There are not enough players for the wipeout to start.");
        }

        if (this.getWipeout().getPlayers().size() == this.getWipeout().getMaxPlayers() || (getTicks() >= 30 && this.getWipeout().getPlayers().size() >= 2)) {
            if (this.getWipeout().getCooldown() == null) {
                this.getWipeout().setCooldown(new Cooldown(11_000));
                this.getWipeout().broadcastMessage("&fThe wipeout will start in &510 seconds&f...");
            } else {
                if (this.getWipeout().getCooldown().hasExpired()) {
                    this.getWipeout().setState(WipeoutState.ROUND_STARTING);
                    this.getWipeout().onRound();
                    this.getWipeout().setTotalPlayers(this.getWipeout().getPlayers().size());
                    this.getWipeout().setEventTask(new WipeoutRoundStartTask(this.getWipeout()));
                    this.getWipeout().getPlayers().forEach(PlayerUtil::denyMovement);
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getWipeout().announce();
        }
    }

}
