package scha.efer.technique.match.task;

import scha.efer.technique.match.Match;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class MatchResetTask extends BukkitRunnable {

    private final Match match;

    @Override
    public void run() {
        if (!match.isHCFMatch() && !match.isKoTHMatch()) {
            if (match.getKit().getGameRules().isBuild() && match.getPlacedBlocks().size() > 0) {
                match.getPlacedBlocks().forEach(l -> l.getBlock().setType(Material.AIR));
                match.getPlacedBlocks().clear();
            }
            if (match.getKit().getGameRules().isBuild() && match.getChangedBlocks().size() > 0) {
                match.getChangedBlocks().forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
                match.getChangedBlocks().clear();
            }
        }
    }

}
