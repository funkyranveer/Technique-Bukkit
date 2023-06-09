package scha.efer.technique.util.essentials.listener;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.bootstrap.BootstrappedListener;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

public class EssentialsListener extends BootstrappedListener {

    private static final List<String> BLOCKED_COMMANDS = Arrays.asList(
            "//calc",
            "//eval",
            "//solve",
            "/bukkit:",
            "/me",
            "/bukkit:me",
            "/minecraft:",
            "/minecraft:me"
    );

    public EssentialsListener(TechniquePlugin Practice) {
        super(Practice);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (BLOCKED_COMMANDS.contains(event.getMessage().toLowerCase())) {
            player.sendMessage(CC.RED + "You cannot perform this command.");
            event.setCancelled(true);
        }
    }

}
