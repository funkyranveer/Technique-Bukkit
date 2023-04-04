package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party leader", "p leader"})
public class PartyLeaderCommand {

    public void execute(Player player, @CPL("player") Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You do not have a party.");
            return;
        }

        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You are not the leader of your party.");
            return;
        }

        if (!profile.getParty().containsPlayer(target)) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "That player is not a member of your party.");
            return;
        }

        if (player.equals(target)) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You cannot yourself leader your party, because you have it already.");
            return;
        }

        profile.getParty().leader(player, target);
    }

}
