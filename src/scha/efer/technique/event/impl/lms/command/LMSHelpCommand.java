package scha.efer.technique.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"ffa", "ffa help", "lms", "lms help"}, permission = "technique.ffahelp")
public class LMSHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&lLMS/FFA &8- &fInformation on how to use ffa/lms commands"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&f/lms cancel &8- &fCancel current LMS/FFA Event"));
        player.sendMessage(Color.translate("&f/lms cooldown &8- &fReset the LMS/FFA Event cooldown"));
        player.sendMessage(Color.translate("&f/lms host &8- &fHost a LMS/FFA Event"));
        player.sendMessage(Color.translate("&f/lms join &8- &fJoin ongoing LMS/FFA Event"));
        player.sendMessage(Color.translate("&f/lms leave &8- &fLeave ongoing LMS/FFA Event"));
        player.sendMessage(Color.translate("&f/lms tp &8- &fTeleport to the LMS/FFA Event Arena"));
        player.sendMessage(Color.translate("&f/lms setspawn  &8- &fSet the spawns for LMS/FFA Event"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}