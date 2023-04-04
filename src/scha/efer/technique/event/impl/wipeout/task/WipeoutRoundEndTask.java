package scha.efer.technique.event.impl.wipeout.task;

import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.event.impl.wipeout.WipeoutState;
import scha.efer.technique.event.impl.wipeout.WipeoutTask;
import org.bukkit.entity.Player;

public class WipeoutRoundEndTask extends WipeoutTask {

    private final Player winner;

    public WipeoutRoundEndTask(Wipeout wipeout, Player winner) {
        super(wipeout, WipeoutState.ROUND_ENDING);
        this.winner = winner;
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getWipeout().end(winner);
        }
    }

}
