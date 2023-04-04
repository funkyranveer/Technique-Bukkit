package scha.efer.technique.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo", "sumo help"}, permission = "technique.sumohelp")
public class SumoHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&lSUMO &8- &fInformation on how to use sumo commands"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&f/sumo cancel &8- &fCancel current sumo Event"));
        player.sendMessage(Color.translate("&f/sumo cooldown &8- &fReset the sumo Event cooldown"));
        player.sendMessage(Color.translate("&f/sumo host &8- &fHost a sumo Event"));
        player.sendMessage(Color.translate("&f/sumo join &8- &fJoin ongoing sumo Event"));
        player.sendMessage(Color.translate("&f/sumo leave &8- &fLeave ongoing sumo Event"));
        player.sendMessage(Color.translate("&f/sumo tp &8- &fTeleport to the sumo Event Arena"));
        player.sendMessage(Color.translate("&f/sumo setspawn  &8- &fSet the spawns for sumo Event"));
        player.sendMessage(Color.translate("&f(One = first spawn, Two = second spawn, Spec = spec spawn"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
