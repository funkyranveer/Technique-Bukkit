package scha.efer.technique.event.impl.sumo.task;

import gg.smok.knockback.KnockbackModule;
import scha.efer.technique.event.impl.sumo.Sumo;
import scha.efer.technique.event.impl.sumo.SumoState;
import scha.efer.technique.event.impl.sumo.SumoTask;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.Cooldown;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

public class SumoStartTask extends SumoTask {

    public SumoStartTask(Sumo sumo) {
        super(sumo, SumoState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getSumo().end();
            return;
        }

        if (this.getSumo().getPlayers().size() <= 1 && this.getSumo().getCooldown() != null) {
            this.getSumo().setCooldown(null);
            this.getSumo().broadcastMessage("&5There are not enough players for the sumo to start.");
        }

        if (this.getSumo().getPlayers().size() == this.getSumo().getMaxPlayers() || (getTicks() >= 30 && this.getSumo().getPlayers().size() >= 2)) {
            if (this.getSumo().getCooldown() == null) {
                this.getSumo().setCooldown(new Cooldown(11_000));
                this.getSumo().broadcastMessage("&fThe sumo will start in &510 seconds&f...");
            } else {
                if (this.getSumo().getCooldown().hasExpired()) {
                    this.getSumo().setState(SumoState.ROUND_STARTING);
                    this.getSumo().onRound();
                    this.getSumo().setTotalPlayers(this.getSumo().getPlayers().size());
                    this.getSumo().setEventTask(new SumoRoundStartTask(this.getSumo()));

                    this.getSumo().getPlayers().forEach(player -> ((CraftPlayer)player).getHandle().setKnockback(KnockbackModule.INSTANCE.profiles.getOrDefault(Kit.getByName("Sumo").getKnockbackProfile(), KnockbackModule.INSTANCE.profiles.get("default"))));
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getSumo().announce();
        }
    }

}
