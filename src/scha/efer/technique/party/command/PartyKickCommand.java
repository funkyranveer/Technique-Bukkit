package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party kick", "p kick"})
public class PartyKickCommand {

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
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You cannot kick yourself from your party.");
            return;
        }

        player.sendMessage(CC.PARTY_PREFIX + CC.GREEN + "Successfully kicked that player");
        target.sendMessage(CC.PARTY_PREFIX + CC.RED + "You have been kicked from the party");
        profile.getParty().leave(target, true);
    }

}
