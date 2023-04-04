package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@CommandMeta(label = {"clan reset"})
public class ClanResetCommand {
    public void execute(CommandSender sender) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "Only console can run this command");
            return;
        }
        Clan.reset();
        sender.sendMessage("All clan info has been cleared");

    }
}
