package scha.efer.technique.commands;

import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;

@CommandMeta(label={"update", "updateladerboards", "leaderboardsupdate"}, permission="technique.dev")
public class UpdateCommand {
    public void execute(Player p) {
        Bukkit.broadcastMessage(CC.translate(" "));
        Bukkit.broadcastMessage(CC.translate("&5&lLeaderboards"));
        Bukkit.broadcastMessage(CC.translate("&f  Updating leaderboards. This could cause minor lag!"));
        Bukkit.broadcastMessage(CC.translate(" "));
        Bukkit.getOnlinePlayers().forEach(all -> all.playSound(all.getLocation(), Sound.FIREWORK_TWINKLE2, 5F, 5F));
        Profile.getProfiles().values().forEach(Profile::save);
        Profile.loadAllProfiles();
        Kit.getKits().forEach(Kit::save);
        Kit.getKits().forEach(Kit::updateKitLeaderboards);
        Profile.loadGlobalLeaderboards();
        Arena.getArenas().forEach(Arena::save);
        Bukkit.broadcastMessage(CC.translate("&aUpdated leaderboards!"));
    }
}
