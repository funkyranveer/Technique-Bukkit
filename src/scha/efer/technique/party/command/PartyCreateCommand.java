package scha.efer.technique.party.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.party.Party;
import scha.efer.technique.party.PartyMessage;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party create", "p create"})
public class PartyCreateCommand {

    public void execute(Player player) {
        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You cannot create a party while frozen.");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() != null) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You already have a party.");
            return;
        }

        if (!profile.isInLobby()) {
            player.sendMessage(CC.PARTY_PREFIX + CC.RED + "You must be in the lobby to create a party.");
            return;
        }

        profile.setParty(new Party(player));
        profile.refreshHotbar();

        player.sendMessage(PartyMessage.CREATED.format());
    }

}
