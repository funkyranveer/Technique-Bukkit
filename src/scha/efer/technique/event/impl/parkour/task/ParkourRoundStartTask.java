package scha.efer.technique.event.impl.parkour.task;

import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.event.impl.parkour.ParkourState;
import scha.efer.technique.event.impl.parkour.ParkourTask;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;

public class ParkourRoundStartTask extends ParkourTask {

    public ParkourRoundStartTask(Parkour parkour) {
        super(parkour, ParkourState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getParkour().broadcastMessage(CC.RED + "The parkour has started!");
            this.getParkour().setEventTask(null);
            this.getParkour().setState(ParkourState.ROUND_FIGHTING);
            this.getParkour().getPlayers().forEach(PlayerUtil::allowMovement);

            this.getParkour().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getParkour().broadcastMessage("&eThe round will start in &d" + seconds + "&e seconds.");
        }
    }

}
