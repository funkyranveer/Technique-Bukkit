package scha.efer.technique.event.impl.juggernaut.player;

import scha.efer.technique.kit.Kit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JuggernautPlayer {

    @Getter
    private final UUID uuid;
    @Getter
    private final String username;
    @Getter
    @Setter
    private boolean isJuggernaut = false;
    @Getter
    @Setter
    private Kit kit = Kit.getByName("Soup");
    @Getter
    @Setter
    private JuggernautPlayerState state = JuggernautPlayerState.WAITING;

    public JuggernautPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
