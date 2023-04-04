package scha.efer.technique.event.impl.juggernaut.task;

import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.event.impl.juggernaut.JuggernautState;
import scha.efer.technique.event.impl.juggernaut.JuggernautTask;
import scha.efer.technique.util.external.CC;

public class JuggernautRoundStartTask extends JuggernautTask {

    public JuggernautRoundStartTask(Juggernaut juggernaut) {
        super(juggernaut, JuggernautState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getJuggernaut().broadcastMessage(CC.RED + "The round has started!");
            this.getJuggernaut().setEventTask(null);
            this.getJuggernaut().setState(JuggernautState.ROUND_FIGHTING);

            this.getJuggernaut().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getJuggernaut().broadcastMessage("&eThe round will start in &d" + seconds + "&e seconds.");
        }
    }

}
