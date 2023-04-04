package scha.efer.technique.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "juggernaut tp", permission = "technique.parkour.tp")
public class JuggernautTpCommand {

    public void execute(Player player) {
        player.teleport(TechniquePlugin.get().getJuggernautManager().getJuggernautSpectator());
        player.sendMessage(CC.GREEN + "Teleported to juggernaut's spawn location.");
    }

}
