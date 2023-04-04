package scha.efer.technique.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"brackets", "brackets help"}, permission = "technique.bracketshelp")
public class BracketsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&lBRACKETS &8- &fInformation on how to use bracket commands"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&f/brackets cancel &8- &fCancel current Brackets Event"));
        player.sendMessage(Color.translate("&f/brackets cooldown &8- &fReset the Brackets Event cooldown"));
        player.sendMessage(Color.translate("&f/brackets host &8- &fHost a Brackets Event"));
        player.sendMessage(Color.translate("&f/brackets join &8- &fJoin ongoing Brackets Event"));
        player.sendMessage(Color.translate("&f/brackets leave &8- &fLeave ongoing Brackets Event"));
        player.sendMessage(Color.translate("&f/brackets tp &8- &fTeleport to the Brackets Event Arena"));
        player.sendMessage(Color.translate("&f/brackets setspawn  &8- &fSet the spawns for Brackets Event"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
