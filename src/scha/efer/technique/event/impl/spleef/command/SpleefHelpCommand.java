package scha.efer.technique.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef", "spleef help"}, permission = "technique.spleefhelp")
public class SpleefHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&lSLPEEF &8- &fInformation on how to use slpeef commands"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&f/slpeef cancel &8- &fCancel current Spleef Event"));
        player.sendMessage(Color.translate("&f/slpeef cooldown &8- &fReset the Spleef Event cooldown"));
        player.sendMessage(Color.translate("&f/slpeef host &8- &fHost a Spleef Event"));
        player.sendMessage(Color.translate("&f/slpeef join &8- &fJoin ongoing Spleef Event"));
        player.sendMessage(Color.translate("&f/slpeef leave &8- &fLeave ongoing Spleef Event"));
        player.sendMessage(Color.translate("&f/slpeef tp &8- &fTeleport to the Spleef Event Arena"));
        player.sendMessage(Color.translate("&f/slpeef setspawn  &8- &fSet the spawns for Spleef Event"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
