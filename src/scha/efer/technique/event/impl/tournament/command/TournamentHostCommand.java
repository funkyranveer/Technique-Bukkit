package scha.efer.technique.event.impl.tournament.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.event.impl.tournament.Tournament;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.CC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandMeta(label = "tournament host", permission = "tournament.host")
public class TournamentHostCommand {

    private static void broadcastMessage(String message) {
        BaseComponent[] component = TextComponent.fromLegacyText(message);
        for (BaseComponent baseComponent : component) {
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament join"));
            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Click to join the Tournament")));
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(component);
        }
    }

    public void execute(CommandSender commandSender, @CPL("ladder") String ladder, @CPL("team size") String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            commandSender.sendMessage(CC.translate("Please put a valid number."));
            return;
        }
        int size = Integer.parseInt(s);
        if (Tournament.CURRENT_TOURNAMENT != null) {
            commandSender.sendMessage(ChatColor.DARK_PURPLE + "The Tournament has already started");
            return;
        }
        Tournament.CURRENT_TOURNAMENT = new Tournament();
        if (size == 1 || size == 2) {
            if (size == 1) Tournament.CURRENT_TOURNAMENT.setTeamCount(1);
            else Tournament.CURRENT_TOURNAMENT.setTeamCount(2);
        } else {
            commandSender.sendMessage(ChatColor.DARK_PURPLE + "Please choose 1 or 2");
            Tournament.CURRENT_TOURNAMENT.cancel();
            Tournament.CURRENT_TOURNAMENT = null;
            return;
        }
        if (Kit.getByName(ladder) != null) {
            Tournament.CURRENT_TOURNAMENT.setLadder(Kit.getByName(ladder));
        } else {
            commandSender.sendMessage(ChatColor.DARK_PURPLE + "Please choose a valid kit");
            Tournament.CURRENT_TOURNAMENT.cancel();
            Tournament.CURRENT_TOURNAMENT = null;
            return;
        }

        Tournament.CURRENT_TOURNAMENT.setLadder(Kit.getByName(ladder));


        if (commandSender instanceof Player) {
            Tournament.CURRENT_TOURNAMENT.setHostType("Player");
        } else if (commandSender instanceof ConsoleCommandSender) {
            Tournament.CURRENT_TOURNAMENT.setHostType("Console");
        }

        String ladderName = Tournament.CURRENT_TOURNAMENT.getLadder().getName();

        broadcastMessage(CC.translate("&5&lTOURNAMENT &f(/join)"));
        broadcastMessage(CC.translate("&fA &5" + ladderName + " (" + size + "v" + size + ") &ftournament has started!"));

        Tournament.RUNNABLE = new BukkitRunnable() {
            private int countdown = 60;

            @Override
            public void run() {
                countdown--;
                if (countdown == 30 || countdown == 10 || countdown <= 3) {
                    if (countdown > 0) {
                        broadcastMessage(CC.translate("&5&lTOURNAMENT &f(/join)"));
                        broadcastMessage(CC.translate("&fA &5" + ladderName + " (" + size + "v" + size + ") &ftournament is starting in &5" + countdown + " seconds&f."));
                    }
                }
                if (countdown <= 0) {
                    Tournament.RUNNABLE = null;
                    cancel();
                    if (Tournament.CURRENT_TOURNAMENT.getParticipatingCount() < 2) {
                        Bukkit.broadcastMessage(CC.RED + "The tournament has been cancelled.");
                        Tournament.CURRENT_TOURNAMENT.cancel();
                        Tournament.CURRENT_TOURNAMENT = null;
                    } else {
                        Tournament.CURRENT_TOURNAMENT.tournamentstart();
					}
                }
            }
        };
        Tournament.RUNNABLE.runTaskTimer(TechniquePlugin.get(), 20, 20);
    }
}


