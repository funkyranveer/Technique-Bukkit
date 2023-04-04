package scha.efer.technique.event.impl.parkour.task;

import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.event.impl.parkour.ParkourState;
import scha.efer.technique.event.impl.parkour.ParkourTask;
import org.bukkit.entity.Player;

public class ParkourRoundEndTask extends ParkourTask {

    private final Player winner;

    public ParkourRoundEndTask(Parkour parkour, Player winner) {
        super(parkour, ParkourState.ROUND_ENDING);
        this.winner = winner;
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getParkour().end(winner);
        }
    }

}
