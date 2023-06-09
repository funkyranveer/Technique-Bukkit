package scha.efer.technique.event.impl.brackets.task;

import scha.efer.technique.event.impl.brackets.Brackets;
import scha.efer.technique.event.impl.brackets.BracketsState;
import scha.efer.technique.event.impl.brackets.BracketsTask;
import scha.efer.technique.util.external.Cooldown;

public class BracketsStartTask extends BracketsTask {

    public BracketsStartTask(Brackets brackets) {
        super(brackets, BracketsState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getBrackets().end();
            return;
        }

        if (this.getBrackets().getPlayers().size() <= 1 && this.getBrackets().getCooldown() != null) {
            this.getBrackets().setCooldown(null);
            this.getBrackets().broadcastMessage("&5There are not enough players for the brackets to start.");
        }

        if (this.getBrackets().getPlayers().size() == this.getBrackets().getMaxPlayers() || (getTicks() >= 30 && this.getBrackets().getPlayers().size() >= 2)) {
            if (this.getBrackets().getCooldown() == null) {
                this.getBrackets().setCooldown(new Cooldown(11_000));
                this.getBrackets().broadcastMessage("&fThe brackets will start in &510 seconds&f...");
            } else {
                if (this.getBrackets().getCooldown().hasExpired()) {
                    this.getBrackets().setState(BracketsState.ROUND_STARTING);
                    this.getBrackets().onRound();
                    this.getBrackets().setTotalPlayers(this.getBrackets().getPlayers().size());
                    this.getBrackets().setEventTask(new BracketsRoundStartTask(this.getBrackets()));
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getBrackets().announce();
        }
    }

}
