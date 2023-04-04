package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.event.impl.tournament.Tournament;
import scha.efer.technique.party.Party;
import scha.efer.technique.party.PartyPrivacy;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party join", "p join"})
public class PartyJoinCommand {

    public void execute(Player player, Player target) {
        if (target == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "A player with that name could not be found.");
            return;
        }

        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You cannot join a party while frozen.");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isBusy(player)) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You can not do that right now");
            return;
        }

        if (profile.getParty() != null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You already have a party.");
            return;
        }

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        Party party = targetProfile.getParty();

        if (party == null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "A party with that name could not be found.");
            return;
        }

        if (party.getPrivacy() == PartyPrivacy.CLOSED) {
            if (party.getInvite(player.getUniqueId()) == null) {
                player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You have not been invited to that party.");
                return;
            }
        }


        if (Tournament.CURRENT_TOURNAMENT != null) {
            for (Player pplayer : party.getPlayers()) {
                if (Tournament.CURRENT_TOURNAMENT.isParticipating(pplayer)) {
                    player.sendMessage(CC.PARTY_PREFIX + CC.RED + "The party is in tournament");
                    return;
                }
            }
        }

        if (party.getPlayers().size() >= party.getLimit()) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "That party is full and cannot hold anymore players.");
            return;
        }

        if (party.getBanned().contains(player)) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You have been banned from that party");
            return;
        }

        party.join(player);
    }

}
