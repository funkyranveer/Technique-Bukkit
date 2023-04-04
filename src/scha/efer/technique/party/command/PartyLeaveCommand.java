package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party leave", "p leave"})
public class PartyLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You do not have a party.");
            return;
        }

        if (profile.getParty().getLeader().getUuid().equals(player.getUniqueId())) {
            profile.getParty().disband();
        } else {
            profile.getParty().leave(player, false);
        }
    }

}
