package scha.efer.technique.event.impl.skywars.task;

import scha.efer.technique.event.impl.skywars.SkyWars;
import scha.efer.technique.event.impl.skywars.SkyWarsState;
import scha.efer.technique.event.impl.skywars.SkyWarsTask;

public class SkyWarsRoundEndTask extends SkyWarsTask {

    public SkyWarsRoundEndTask(SkyWars skyWars) {
        super(skyWars, SkyWarsState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            if (this.getSkyWars().canEnd()) {
                this.getSkyWars().end();
            }
        }
    }

}
