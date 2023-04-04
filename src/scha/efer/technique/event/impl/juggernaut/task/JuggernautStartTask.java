package scha.efer.technique.event.impl.juggernaut.task;

import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.event.impl.juggernaut.JuggernautState;
import scha.efer.technique.event.impl.juggernaut.JuggernautTask;
import scha.efer.technique.event.impl.juggernaut.player.JuggernautPlayer;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.Cooldown;

public class JuggernautStartTask extends JuggernautTask {

    public JuggernautStartTask(Juggernaut juggernaut) {
        super(juggernaut, JuggernautState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getJuggernaut().end("None");
            return;
        }

        if (this.getJuggernaut().getPlayers().size() <= 1 && this.getJuggernaut().getCooldown() != null) {
            this.getJuggernaut().setCooldown(null);
            this.getJuggernaut().broadcastMessage("&5There are not enough players for the juggernaut to start.");
        }

        if (this.getJuggernaut().getPlayers().size() == this.getJuggernaut().getMaxPlayers() || (getTicks() >= 30 && this.getJuggernaut().getPlayers().size() >= 2)) {
            if (this.getJuggernaut().getCooldown() == null) {
                this.getJuggernaut().setCooldown(new Cooldown(11_000));
                this.getJuggernaut().broadcastMessage("&fThe juggernaut will start in &510 seconds&f...");
            } else {
                if (this.getJuggernaut().getCooldown().hasExpired()) {
                    this.getJuggernaut().setState(JuggernautState.ROUND_STARTING);
                    JuggernautPlayer temp = this.getJuggernaut().getEventPlayer(this.getJuggernaut().getRemainingPlayers().get(this.getJuggernaut().getRandomPlayer()));
                    temp.setJuggernaut(true);
                    temp.setKit(Kit.getByName("NoDebuff"));
                    this.getJuggernaut().setJuggernaut(temp);
                    this.getJuggernaut().onRound();
                    this.getJuggernaut().setTotalPlayers(this.getJuggernaut().getPlayers().size());
                    this.getJuggernaut().setEventTask(new JuggernautRoundStartTask(this.getJuggernaut()));
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getJuggernaut().announce();
        }
    }

}
