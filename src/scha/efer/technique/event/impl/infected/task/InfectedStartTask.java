package scha.efer.technique.event.impl.infected.task;

import scha.efer.technique.event.impl.infected.Infected;
import scha.efer.technique.event.impl.infected.InfectedState;
import scha.efer.technique.event.impl.infected.InfectedTask;
import scha.efer.technique.event.impl.infected.player.InfectedPlayer;
import scha.efer.technique.event.impl.infected.player.InfectedPlayerState;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.Cooldown;

public class InfectedStartTask extends InfectedTask {

    public InfectedStartTask(Infected infected) {
        super(infected, InfectedState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getInfected().end("None");
            return;
        }

        if (this.getInfected().getPlayers().size() <= 1 && this.getInfected().getCooldown() != null) {
            this.getInfected().setCooldown(null);
            this.getInfected().broadcastMessage("&5There are not enough players for the infected to start.");
        }

        if (this.getInfected().getPlayers().size() == this.getInfected().getMaxPlayers() || (getTicks() >= 30 && this.getInfected().getPlayers().size() >= 3)) {
            if (this.getInfected().getCooldown() == null) {
                this.getInfected().setCooldown(new Cooldown(11_000));
                this.getInfected().broadcastMessage("&fThe infected will start in &510 seconds&f...");
            } else {
                if (this.getInfected().getCooldown().hasExpired()) {
                    this.getInfected().setState(InfectedState.ROUND_STARTING);
                    InfectedPlayer temp = this.getInfected().getEventPlayer(this.getInfected().getRemainingPlayers().get(this.getInfected().getRandomPlayer()));
                    temp.setInfected(true);
                    temp.setState(InfectedPlayerState.INFECTED);
                    temp.setKit(Kit.getByName("NoDebuff"));
                    this.getInfected().setInfected(temp);
                    this.getInfected().onRound();
                    this.getInfected().setTotalPlayers(this.getInfected().getPlayers().size());
                    this.getInfected().setEventTask(new InfectedRoundStartTask(this.getInfected()));
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getInfected().announce();
        }
    }

}
