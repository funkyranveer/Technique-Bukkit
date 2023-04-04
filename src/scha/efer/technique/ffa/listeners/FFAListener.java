package scha.efer.technique.ffa.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;

public class FFAListener implements Listener {

    @EventHandler
    public void onFFAJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
    }

    @EventHandler
    public void onFFALeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getState() == ProfileState.IN_FFA) {
            if (player.isOnline()) {
                player.getInventory().clear();
                profile.setState(ProfileState.IN_LOBBY);
                profile.refreshHotbar();
            }
        }
    }

}
