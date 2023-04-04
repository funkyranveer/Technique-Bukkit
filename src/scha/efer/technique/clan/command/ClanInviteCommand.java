package scha.efer.technique.clan.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanPlayer;
import scha.efer.technique.clan.ClanRole;
import scha.efer.technique.util.Color;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"clan invite"})
public class ClanInviteCommand {
    public void execute(Player player, Player other) {
        if (Clan.getByMember(player.getUniqueId()) == null) {
            player.sendMessage(Color.translate("&5You are not in a clan"));
            return;
        }

        if (other == null) {
            player.sendMessage(Color.translate("&5That player is not online"));
            return;
        }

        if (!other.isOnline()) {
            player.sendMessage(Color.translate("&5That player is not online"));
            return;
        }

        Clan clan = Clan.getByMember(player.getUniqueId());
        ClanPlayer me = clan.getClanPlayer(player.getUniqueId());
        if (me.getRole() != ClanRole.LEADER && me.getRole() != ClanRole.CAPTAIN) {
            player.sendMessage(ChatColor.DARK_PURPLE + "Only clan leaders and captains may do this");
            return;
        }
        if (clan.getInvitations().contains(other.getUniqueId())) {
            player.sendMessage(ChatColor.DARK_PURPLE + "This player has already been invited to your clan!");
            return;
        }
        if (Clan.getByMember(other.getUniqueId()) == clan) {
            player.sendMessage(ChatColor.DARK_PURPLE + "This player is already in your clan!");
            return;
        }
        clan.getInvitations().add(other.getUniqueId());
        BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.WHITE + "You have been invited by " + ChatColor.DARK_PURPLE + player.getName() + ChatColor.GREEN + " [Click here to join]");
        for (BaseComponent component : components) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan join " + clan.getName()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Join " + clan.getName())));
        }
        other.spigot().sendMessage(components);
        player.sendMessage(Color.translate("&fYou have invited &a" + other.getName() + " &fto your clan!"));

    }
}
