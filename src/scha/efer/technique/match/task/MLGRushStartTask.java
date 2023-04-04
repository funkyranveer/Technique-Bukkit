package scha.efer.technique.match.task;

import lombok.SneakyThrows;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.MatchState;
import scha.efer.technique.match.impl.MLGRushMatch;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;

public class MLGRushStartTask extends BukkitRunnable {

    private final MLGRushMatch match;
    private int ticks;

    public MLGRushStartTask(Match match) {
        this.match = (MLGRushMatch) match;
    }

    @SneakyThrows
    @Override
    public void run() {
        int seconds=5 - ticks;
        if (match.isEnding()) {
            cancel();
            return;
        }

        if (seconds == 2) {
            match.getPlayers().forEach(PlayerUtil::allowMovement);
            match.setState(MatchState.FIGHTING);
            match.setStartTimestamp(System.currentTimeMillis());
            match.broadcastMessage(CC.GREEN + "The round has started!");
            match.broadcastSound(Sound.NOTE_BASS);
            for ( Player player : match.getPlayers() ) {
                player.updateInventory();
                for ( Player oPlayer : match.getPlayers() ) {
                    if (player.equals(oPlayer))
                        continue;
                    player.showPlayer(oPlayer);
                    oPlayer.showPlayer(player);
                }
            }
            cancel();
            return;
        }
        match.broadcastMessage(CC.WHITE + "The match will begin in " + CC.DARK_PURPLE + seconds + CC.WHITE + "...");
        match.broadcastSound(Sound.NOTE_PLING);
        ticks++;

    }

}
