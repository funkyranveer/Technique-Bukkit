package scha.efer.technique.mlgrush;

import org.bukkit.entity.Player;
import scha.efer.technique.mlgrush.type.LocationType;

import java.util.HashMap;
import java.util.UUID;

public class MLGRushManager {

    private Player player;
    private static HashMap<UUID, LocationType> currentSetupBed = new HashMap<>();

    public MLGRushManager(Player player) {
        this.player = player;
    }

    public LocationType getCurrentSetupBed() {
        return currentSetupBed.getOrDefault(player.getUniqueId(), null);
    }

    /**
     * Sets the bed the player has to setup now.
     * @param locationType The type of the location. (Must be a bed location!)
     */
    public void setCurrentSetupBed(final LocationType locationType) {
        if(locationType == null)
            currentSetupBed.remove(player.getUniqueId());
        currentSetupBed.put(player.getUniqueId(), locationType);
    }

    public Player getPlayer() {
        return player;
    }



}
