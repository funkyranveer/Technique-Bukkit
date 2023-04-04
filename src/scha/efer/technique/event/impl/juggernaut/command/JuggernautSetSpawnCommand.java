package scha.efer.technique.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "juggernaut setspawn", permission = "technique.juggernaut.setspawn")
public class JuggernautSetSpawnCommand {

    public void execute(Player player) {
        TechniquePlugin.get().getJuggernautManager().setJuggernautSpectator(player.getLocation());

        player.sendMessage(CC.GREEN + "Updated juggernaut's spawn location.");

        TechniquePlugin.get().getJuggernautManager().save();
    }

}
