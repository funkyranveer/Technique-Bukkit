package scha.efer.technique.duel.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.duel.DuelProcedure;
import scha.efer.technique.duel.menu.DuelSelectKitMenu;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "duel")
public class DuelCommand {

    public void execute(Player player, @CPL("player") Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "A player with that name could not be found.");
            return;
        }

        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot duel a player while being frozen.");
            return;
        }

        if (target.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot duel a player who's frozen.");
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(CC.RED + "You cannot duel yourself.");
            return;
        }

        Profile senderProfile = Profile.getByUuid(player.getUniqueId());
        Profile receiverProfile = Profile.getByUuid(target.getUniqueId());

        if (senderProfile.isBusy(player)) {
            player.sendMessage(CC.RED + "You cannot duel anyone right now.");
            return;
        }

        if (receiverProfile.isBusy(target)) {
            player.sendMessage(CC.translate(CC.RED + target.getDisplayName()) + CC.RED + " is currently busy.");
            return;
        }

        if (!receiverProfile.getOptions().isReceiveDuelRequests()) {
            player.sendMessage(CC.RED + "That player is not accepting any duel requests at the moment.");
            return;
        }

        if (!senderProfile.canSendDuelRequest(player)) {
            player.sendMessage(CC.RED + "You have already sent that player a duel request.");
            return;
        }

        if (senderProfile.getParty() != null && receiverProfile.getParty() == null) {
            player.sendMessage(CC.RED + "That player is not in a party.");
            return;
        }

        DuelProcedure procedure = new DuelProcedure(player, target, senderProfile.getParty() != null);
        senderProfile.setDuelProcedure(procedure);

        new DuelSelectKitMenu("normal").openMenu(player);
    }

}
