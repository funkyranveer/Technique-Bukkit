package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.party.PartyPrivacy;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"p close", "party close"})
public class PartyCloseCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You do not have a party.");
            return;
        }

        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You are not the leader of your party.");
            return;
        }

        profile.getParty().setPrivacy(PartyPrivacy.CLOSED);
    }

}
