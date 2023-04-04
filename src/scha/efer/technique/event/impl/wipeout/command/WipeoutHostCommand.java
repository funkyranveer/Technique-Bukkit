package scha.efer.technique.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"wipeout host"}, permission = "technique.wipeout.host")
public class WipeoutHostCommand {

    public static void execute(Player player) {
        if (TechniquePlugin.get().getWipeoutManager().getActiveWipeout() != null) {
            player.sendMessage(CC.RED + "There is already an active Wipeout Event.");
            return;
        }

        if (!TechniquePlugin.get().getWipeoutManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is an active cooldown for the Wipeout Event.");
            return;
        }

        TechniquePlugin.get().getWipeoutManager().setActiveWipeout(new Wipeout(player));

        for (Player other : TechniquePlugin.get().getServer().getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(other.getUniqueId());

            if (profile.isInLobby()) {
                if (!profile.getKitEditor().isActive()) {
                    profile.refreshHotbar();
                }
            }
        }
    }

}
