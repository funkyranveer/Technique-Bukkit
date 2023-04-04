package scha.efer.technique.match.task;

import scha.efer.technique.match.Match;
import scha.efer.technique.match.MatchState;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartTask extends BukkitRunnable {

    private final Match match;
    private int ticks;

    public MatchStartTask(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        int seconds = 3 - ticks;

        if (match.isEnding()) {
            cancel();
            return;
        }

        if (match.isHCFMatch() || match.isKoTHMatch()) {
            if (seconds == 0) {
                match.setState(MatchState.FIGHTING);
                match.setStartTimestamp(System.currentTimeMillis());
                match.broadcastMessage(CC.GREEN + "Match started!");
                match.broadcastSound(Sound.FIREWORK_LARGE_BLAST);
                cancel();
                return;
            }

            match.broadcastMessage(CC.WHITE + "The match will begin in " + CC.DARK_PURPLE + seconds + CC.WHITE + "...");
            match.broadcastSound(Sound.ORB_PICKUP);
        } else {
            if (match.getKit().getGameRules().isSumo() || match.getKit().getGameRules().isParkour()) {
                if (seconds == 0) {
                    match.getPlayers().forEach(PlayerUtil::allowMovement);
                    match.setState(MatchState.FIGHTING);
                    match.setStartTimestamp(System.currentTimeMillis());
                    match.broadcastMessage(CC.GREEN + "Match started!");
                    match.broadcastSound(Sound.FIREWORK_LARGE_BLAST);
                    for (Player player : match.getPlayers()) {
                        player.getInventory().remove(Material.INK_SACK);
                        player.updateInventory();
                        for (Player oPlayer : match.getPlayers()) {
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
                match.broadcastSound(Sound.ORB_PICKUP);
            } else if (match.getKit().getName().equalsIgnoreCase("MLG-Rush")){
                if (seconds == 0) {
                    match.getPlayers().forEach(PlayerUtil::allowMovement);
                    match.setState(MatchState.FIGHTING);
                    match.setStartTimestamp(System.currentTimeMillis());
                    match.broadcastMessage(CC.GREEN + "Match started!");
                    match.broadcastSound(Sound.FIREWORK_LARGE_BLAST);
                    match.getPlayers().forEach(player -> {
                        player.getInventory().remove(Material.INK_SACK);
                        player.updateInventory();
                    });
                    cancel();
                    return;
                }

                match.broadcastMessage(CC.WHITE + "The match will begin in " + CC.DARK_PURPLE + seconds + CC.WHITE + "...");
                match.broadcastSound(Sound.ORB_PICKUP);
            } else {
                if (seconds == 0) {
                    match.setState(MatchState.FIGHTING);
                    match.setStartTimestamp(System.currentTimeMillis());
                    match.broadcastMessage(CC.GREEN + "Match started!");
                    match.broadcastSound(Sound.FIREWORK_LARGE_BLAST);
                    match.getPlayers().forEach(player -> {
                        player.getInventory().remove(Material.INK_SACK);
                        player.updateInventory();
                    });
                    cancel();
                    return;
                }

                match.broadcastMessage(CC.WHITE + "The match will begin in " + CC.DARK_PURPLE + seconds + CC.WHITE + "...");
                match.broadcastSound(Sound.ORB_PICKUP);
            }
        }

        ticks++;
    }

}
