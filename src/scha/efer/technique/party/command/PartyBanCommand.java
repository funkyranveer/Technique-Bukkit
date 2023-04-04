package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party ban", "p ban"})
public class PartyBanCommand {

    public void execute(Player player, @CPL("player") Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You do not have a party.");
            return;
        }

        if (target == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "That player is not online");
            return;
        }

        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You are not the leader of your party.");
            return;
        }

        if (player.equals(target)) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You cannot ban yourself from your party.");
            return;
        }

        if (profile.getParty().getBanned().contains(target)) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "That player is already banned.");
            return;
        }

        if (profile.getParty().containsPlayer(target)) {
            profile.getParty().leave(target, true);
        }

        player.sendMessage(CC.PARTY_PREFIX + CC.GREEN + "Successfully banned that player");
        profile.getParty().ban(target);
    }

}
