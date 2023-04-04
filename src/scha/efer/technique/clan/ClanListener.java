package scha.efer.technique.clan;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ClanListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Clan clan = Clan.getByMember(event.getPlayer().getUniqueId());
                if (clan != null) {
                    clan.apply(event.getPlayer());
                    clan.broadcast("&3[Clans] &d" + TechniquePlugin.get().getSmokHook().getPlayerColor(event.getPlayer()) + " &ehas joined.");
                }
            }
        }.runTaskLater(TechniquePlugin.get(), 20L);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (event.getMessage().startsWith("!")) {
            event.setCancelled(true);
            Clan clan = Clan.getByMember(event.getPlayer().getUniqueId());
            if (clan == null) {
                event.getPlayer().sendMessage(CC.RED + "You are not in a clan!");
                return;
            }
            clan.broadcast(CC.DARK_AQUA + "[Clans]" + CC.RESET + " " +
                    CC.translate(TechniquePlugin.get().getSmokHook().getPlayerPrefix(event.getPlayer()) + event.getPlayer().getName()) + CC.RESET + ": " +
                    ChatColor.stripColor(event.getMessage().substring(1)));
        }
    }
}
