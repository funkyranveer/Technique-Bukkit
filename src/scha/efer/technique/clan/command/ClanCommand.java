package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"clan"})
public class ClanCommand {
    public void execute(Player player) {
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&5&lCLANS &8- &fInformation on how to use clan commands"));
        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");

        player.sendMessage(Color.translate("&5General Commands:"));
        player.sendMessage(Color.translate("&f/clan create &8-&f Create a clan"));
        player.sendMessage(Color.translate("&f/clan leave &8-&f Leave your current clan"));
        player.sendMessage(Color.translate("&f/clan accept [clan|player] &8-&f Accept a clan invitation"));
        player.sendMessage(Color.translate("&f/clan info [clan|player] &8-&f Display a clan information"));
        player.sendMessage(" ");
        player.sendMessage(Color.translate("&5Leader Commands:"));
        player.sendMessage(Color.translate("&f/clan disband &8-&f Disband your clan"));
        player.sendMessage(Color.translate("&f/clan desc &8-&f Set your clan's announcement"));
        player.sendMessage(Color.translate("&f/clan promote &8-&f Add or remove captain"));
        player.sendMessage(" ");
        player.sendMessage(Color.translate("&5Captain Commands:"));
        player.sendMessage(Color.translate("&f/clan invite <player> &8-&f Invite someone to your clan"));
        player.sendMessage(Color.translate("&f/clan kick <player> &8-&f kick a player from your clan"));

        player.sendMessage(" ");
        player.sendMessage(Color.translate("&5Other help:"));
        player.sendMessage(Color.translate("&fTo use &5clan chat&f, prefix your message with a &f'&5!&f'&f sign."));

        player.sendMessage(ChatColor.WHITE.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");


    }
}
