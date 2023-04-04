package scha.efer.technique.event.impl.parkour.task;

import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.event.impl.parkour.ParkourState;
import scha.efer.technique.event.impl.parkour.ParkourTask;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.Cooldown;

public class ParkourStartTask extends ParkourTask {

    public ParkourStartTask(Parkour parkour) {
        super(parkour, ParkourState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getParkour().end(null);
            return;
        }

        if (this.getParkour().getPlayers().size() <= 1 && this.getParkour().getCooldown() != null) {
            this.getParkour().setCooldown(null);
            this.getParkour().broadcastMessage("&5There are not enough players for the parkour to start.");
        }

        if (this.getParkour().getPlayers().size() == this.getParkour().getMaxPlayers() || (getTicks() >= 30 && this.getParkour().getPlayers().size() >= 2)) {
            if (this.getParkour().getCooldown() == null) {
                this.getParkour().setCooldown(new Cooldown(11_000));
                this.getParkour().broadcastMessage("&fThe parkour will start in &510 seconds&f...");
            } else {
                if (this.getParkour().getCooldown().hasExpired()) {
                    this.getParkour().setState(ParkourState.ROUND_STARTING);
                    this.getParkour().onRound();
                    this.getParkour().setTotalPlayers(this.getParkour().getPlayers().size());
                    this.getParkour().setEventTask(new ParkourRoundStartTask(this.getParkour()));
                    this.getParkour().getPlayers().forEach(PlayerUtil::denyMovement);
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getParkour().announce();
        }
    }

}
