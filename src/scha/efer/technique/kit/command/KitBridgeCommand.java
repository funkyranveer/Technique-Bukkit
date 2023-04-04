package scha.efer.technique.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit bridge"}, permission = "technique.dev")
public class KitBridgeCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&c")) + "Kit does not exist");
        } else {
            if (kit.getGameRules().isBridge()) {
                kit.getGameRules().setBridge(false);
            } else if (!kit.getGameRules().isBridge()) {
                kit.getGameRules().setBridge(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&7Updated build mode for &5" + kit.getName() +  " &7to &5" + (kit.getGameRules().isBridge() ? "true!" : "false!")));
        }
    }
}

