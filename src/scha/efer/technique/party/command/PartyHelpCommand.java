package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party help", "party", "p help", "p"})
public class PartyHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&lPARTYS &8- &fInformation on how to use party commands"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5General Commands:"));
        player.sendMessage(Color.translate("&f/party create <Name> &8- &fCreate a party"));
        player.sendMessage(Color.translate("&f/party leave &8- &fLeave your party"));
        player.sendMessage(Color.translate("&f/party join <Name> &8- &fJoin a party"));
        player.sendMessage(Color.translate("&f"));
        player.sendMessage(Color.translate("&5Leader Commands:"));
        player.sendMessage(Color.translate("&f/party disband &8- &fDisband your party"));
        player.sendMessage(Color.translate("&f/party kick <Name> &8- &fKick a player"));
        player.sendMessage(Color.translate("&f/party leader &8- &fMake a player leader"));
        player.sendMessage(Color.translate("&f/party open &8- &fMake your party open"));
        player.sendMessage(Color.translate("&f/party close &8- &fMake your party closed"));
        player.sendMessage(Color.translate("&f"));
        player.sendMessage(Color.translate("&fTo use the &5party chat&f, prefix your message with a &f'&5?&f' sign"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
