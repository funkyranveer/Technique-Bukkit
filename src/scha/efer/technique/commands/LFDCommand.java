package scha.efer.technique.commands;

import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;

@CommandMeta(label={"lfd", "lookingforduel", "lookingfd"})
public class LFDCommand {
    public void execute(Player p) {
        Bukkit.broadcastMessage(CC.translate("&5[Looking for Duel] &d" + p.getName() + "&f is looking for someone to fight! Think you can win?"));
    }
}
