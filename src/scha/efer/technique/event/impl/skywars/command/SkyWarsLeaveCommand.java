package scha.efer.technique.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.skywars.SkyWars;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars leave")
public class SkyWarsLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        SkyWars activeSkyWars = TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars();

        if (activeSkyWars == null) {
            player.sendMessage(CC.RED + "There isn't any active SkyWars Events.");
            return;
        }

        if (!profile.isInSkyWars() || !activeSkyWars.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active SkyWars Event.");
            return;
        }

        TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars().handleLeave(player);
    }

}
