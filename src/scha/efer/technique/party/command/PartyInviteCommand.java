package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.party.PartyPrivacy;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party invite", "p invite"})
public class PartyInviteCommand {

    public void execute(Player player, Player target) {
        if (target == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "A player with that name could not be found.");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You do not have a party.");
            return;
        }

        if (profile.getParty().getInvite(target.getUniqueId()) != null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "That player has already been invited to your party.");
            return;
        }

        if (profile.getParty().containsPlayer(target)) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "That player is already in your party.");
            return;
        }

        if (profile.getParty().getPrivacy() == PartyPrivacy.OPEN) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "The party state is Open. You do not need to invite players.");
            return;
        }

        Profile targetData = Profile.getByUuid(target.getUniqueId());

        if (targetData.isBusy(target)) {
            player.sendMessage(CC.PARTY_PREFIX + target.getDisplayName() + CC.RED + " is currently busy.");
            return;
        }

        profile.getParty().invite(target);
    }

}
