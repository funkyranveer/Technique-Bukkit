package scha.efer.technique.match.task;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.*;
import scha.efer.technique.match.Match;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;

public class MatchWaterCheckTask extends BukkitRunnable {
    private final Match match;

    public MatchWaterCheckTask(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        if (match == null || match.getAlivePlayers().isEmpty() || match.getAlivePlayers().size() <= 1) {
            return;
        }

        for (Player player : match.getAlivePlayers()) {
            if (player == null || Profile.getByUuid(player.getUniqueId()).getState() != ProfileState.IN_FIGHT) {
                return;
            }

            Block body = player.getLocation().getBlock();
            Block head = body.getRelative(BlockFace.UP);
            if (body.getType() == Material.WATER || body.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                match.handleDeath(player, null, false);
            }
        }
    }
}
