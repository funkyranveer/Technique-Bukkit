package scha.efer.technique.commands;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;

@CommandMeta(label="setelo", permission="technique.staff")
public class SetEloCommand {
    public void execute(Player player, @CPL("profile") String name, @CPL("[global|kit]") String type, @CPL("amount") String inter ) {
        Player target= PlayerUtil.getPlayer(name);
        int elo = Integer.parseInt(inter);
        if (target != null) {
            Profile profile=Profile.getByUuid(target);

            if (type.equalsIgnoreCase("global")) {
                profile.setGlobalElo(elo);
                player.sendMessage(CC.translate("&fUpdated Global elo for &5" + name));
                return;
            }

            if (Kit.getByName(type) == null) {
                player.sendMessage(CC.translate("&fKit &5" + name + " &fdoesn't exist!"));
            } else {
                Kit kit=Kit.getByName(type);
                profile.getKitData().get(kit).setElo(elo);
                player.sendMessage(CC.translate("&fUpdated &5" + type + "'s&f elo for &5" + name));
                profile.calculateGlobalElo();
            }
        } else {
            player.sendMessage(CC.translate("&cPlayer not found or is not online!"));
        }
    }
}
