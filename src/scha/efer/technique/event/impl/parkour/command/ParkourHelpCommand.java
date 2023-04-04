package scha.efer.technique.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"parkour", "parkour help"}, permission = "technique.parkourhelp")
public class ParkourHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&lPARKOUR &8- &fInformation on how to use parkour commands"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&f/parkour cancel &8- &fCancel current Parkour Event"));
        player.sendMessage(Color.translate("&f/parkour cooldown &8- &fReset the Parkour Event cooldown"));
        player.sendMessage(Color.translate("&f/parkour host &8- &fHost a Parkour Event"));
        player.sendMessage(Color.translate("&f/parkour join &8- &fJoin ongoing Parkour Event"));
        player.sendMessage(Color.translate("&f/parkour leave &8- &fLeave ongoing Parkour Event"));
        player.sendMessage(Color.translate("&f/parkour tp &8- &fTeleport to the Parkour Event Arena"));
        player.sendMessage(Color.translate("&f/parkour setspawn  &8- &fSet the spawns for Parkour Event"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
