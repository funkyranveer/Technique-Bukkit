package scha.efer.technique.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.arena.selection.GoldenHeads;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"arena goldenhead"}, permission="technique.dev")
public class ArenaGoldenHeadCommand {
    public void execute(Player p, @CPL("[normal|bridge]") String type) {
        if (type.equalsIgnoreCase("bridge")) {
            p.sendMessage(CC.translate("&7You received a &5Adam's Apple&7."));
            p.getInventory().addItem(GoldenHeads.getBridgeApple());
            return;
        } else if (type.equalsIgnoreCase("normal")) {
            p.sendMessage(CC.translate("&7You received a &5Golden head&7."));
            p.getInventory().addItem(GoldenHeads.goldenHeadItem());
            return;
        }
        p.sendMessage(CC.translate("&7Please pick specify &5'normal' &7or &5'bridge' &7type."));
    }
}
