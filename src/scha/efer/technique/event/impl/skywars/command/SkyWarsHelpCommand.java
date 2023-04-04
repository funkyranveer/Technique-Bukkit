package scha.efer.technique.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"skywars", "skywars help"}, permission = "technique.skywarshelp")
public class SkyWarsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&lSKYWARS &8- &fInformation on how to use skywars commands"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&f/skywars cancel &8- &fCancel current Skywars Event"));
        player.sendMessage(Color.translate("&f/skywars cooldown &8- &fReset the Skywars Event cooldown"));
        player.sendMessage(Color.translate("&f/skywars host &8- &fHost a Skywars Event"));
        player.sendMessage(Color.translate("&f/skywars join &8- &fJoin ongoing Skywars Event"));
        player.sendMessage(Color.translate("&f/skywars leave &8- &fLeave ongoing Skywars Event"));
        player.sendMessage(Color.translate("&f/skywars tp &8- &fTeleport to the Skywars Event Arena"));
        player.sendMessage(Color.translate("&f/parkour setspawn  &8- &fSet the spawns for Skywars Event"));
        player.sendMessage(Color.translate("&f(You can setup too 12 setspawns"));
        player.sendMessage(Color.translate("&f/parkour setchest <chest>  &8- &fSet the chest for Skywars Event"));
        player.sendMessage(Color.translate("&f(Chests: ISLAND, NORMAL, MID)"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
