package scha.efer.technique.match.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "stopspectating")
public class StopSpectatingCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isInFight() && !profile.getMatch().getTeamPlayer(player).isAlive()) {
            profile.getMatch().getTeamPlayer(player).setDisconnected(true);
            profile.setState(ProfileState.IN_LOBBY);
            profile.setMatch(null);

        } else if (profile.isSpectating()) {

            profile.setSpectating(null);

            if (profile.getMatch() != null) {
                profile.getMatch().removeSpectator(player);
            } else if (profile.getSumo() != null) {
                profile.getSumo().removeSpectator(player);
            } else if (profile.getBrackets() != null) {
                profile.getBrackets().removeSpectator(player);
            } else if (profile.getLms() != null) {
                profile.getLms().removeSpectator(player);
            } else if (profile.getJuggernaut() != null) {
                profile.getJuggernaut().removeSpectator(player);
            } else if (profile.getParkour() != null) {
                profile.getParkour().removeSpectator(player);
            } else if (profile.getWipeout() != null) {
                profile.getWipeout().removeSpectator(player);
            } else if (profile.getSkyWars() != null) {
                profile.getSkyWars().removeSpectator(player);
            } else if (profile.getSpleef() != null) {
                profile.getSpleef().removeSpectator(player);
            }
        } else {
            player.sendMessage(CC.RED + "You are not spectating a match or event.");
        }
    }

}
