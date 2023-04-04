package scha.efer.technique.event.impl.wipeout.task;

import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.event.impl.wipeout.WipeoutState;
import scha.efer.technique.event.impl.wipeout.WipeoutTask;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;

public class WipeoutRoundStartTask extends WipeoutTask {

    public WipeoutRoundStartTask(Wipeout wipeout) {
        super(wipeout, WipeoutState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getWipeout().broadcastMessage(CC.RED + "The wipeout has started!");
            this.getWipeout().setEventTask(null);
            this.getWipeout().setState(WipeoutState.ROUND_FIGHTING);
            this.getWipeout().getPlayers().forEach(PlayerUtil::allowMovement);

            this.getWipeout().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getWipeout().broadcastMessage("&eThe round will start in &d" + seconds + "&e seconds.");
        }
    }

}
