package scha.efer.technique.event.impl.juggernaut.task;

import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.event.impl.juggernaut.JuggernautState;
import scha.efer.technique.event.impl.juggernaut.JuggernautTask;

public class JuggernautRoundEndTask extends JuggernautTask {

    public JuggernautRoundEndTask(Juggernaut juggernaut) {
        super(juggernaut, JuggernautState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            if (!this.getJuggernaut().canEnd().equalsIgnoreCase("None")) {
                this.getJuggernaut().end(this.getJuggernaut().canEnd());
            }
        }
    }

}
