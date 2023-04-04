package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.clan.ClanRole;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandMeta(label = {"clan disband"})
public class ClanDisbandCommand {
    public void execute(Player player) {
        if (Clan.getByMember(player.getUniqueId()) == null) {
            player.sendMessage(CC.RED + "You are not in a clan");
            return;
        }

        Clan clan = Clan.getByMember(player.getUniqueId());
        ClanPlayer me = clan.getClanPlayer(player.getUniqueId());
        if (me.getRole() != ClanRole.LEADER) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Only clan leaders may do this");
            return;
        }
        for (Player other : clan.getPlayerWhereOnline()) {
            Profile profile = Profile.getByUuid(other.getUniqueId());
            if (profile != null && profile.getMatch() != null) {
                player.sendMessage(ChatColor.DARK_PURPLE + "You can not disband while " + other.getName() + " is in a match!");
                return;
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                clan.delete();
                player.sendMessage(ChatColor.DARK_PURPLE + "You have disbanded this clan.");
            }
        }.runTaskAsynchronously(TechniquePlugin.get());

    }
}
