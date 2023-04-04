package scha.efer.technique.mlgrush.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.match.MatchState;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;

public class MLGRushListeners implements Listener {

    @EventHandler
    public void onBreak(final BlockBreakEvent e) {
        try {
            final Player p = e.getPlayer();
            final Block b = e.getBlock();
            final Location loc = b.getLocation();
            final Material m = b.getType();
            FileConfiguration config = TechniquePlugin.get().getArenasConfig().getConfiguration();
            Profile profile = Profile.getByUuid(p.getUniqueId());
            Profile profileB = Profile.getByUuid(profile.getMatch().getTeamPlayerB().getUuid());
            Profile profileA = Profile.getByUuid(profile.getMatch().getTeamPlayerA().getUuid());
            if (profile.getMatch().getKit().getName().equalsIgnoreCase("MLG-Rush")) {
                /*final World w1 = Bukkit.getServer().getWorld("world");
                final int x1 = config.getInt("arenas." + profile.getMatch().getArena().getName() + ".blue_bed.x");
                final int y1 = config.getInt("arenas." + profile.getMatch().getArena().getName() + ".blue_bed.y");
                final int z1 = config.getInt("arenas." + profile.getMatch().getArena().getName() + ".blue_bed.z");
                final World w2 = Bukkit.getServer().getWorld("world");
                final int x2 = config.getInt("arenas." + profile.getMatch().getArena().getName() + ".red_bed.x");
                final int y2 = config.getInt("arenas." + profile.getMatch().getArena().getName() + ".red_bed.y");
                final int z2 = config.getInt("arenas." + profile.getMatch().getArena().getName() + ".red_bed.z");*/
                if (m == Material.BED_BLOCK && loc.getBlockX() == profile.getMatch().getArena().getBedLocationB().getBlockX() && loc.getBlockZ() == profile.getMatch().getArena().getBedLocationB().getBlockZ()) {
                //if (m == Material.BED_BLOCK && loc.getWorld() == w1 && ((loc.getBlockX() == x1 && loc.getBlockY() == y1 && loc.getBlockZ() == z1) || (loc.getBlockX() == x1 + 1 && loc.getBlockY() == y1 && loc.getBlockZ() == z1) || (loc.getBlockX() == x1 - 1 && loc.getBlockY() == y1 && loc.getBlockZ() == z1) || (loc.getBlockX() == x1 && loc.getBlockY() == y1 && loc.getBlockZ() == z1 + 1) || (loc.getBlockX() == x1 && loc.getBlockY() == y1 && loc.getBlockZ() == z1 - 1))) {
                    if (profile.getMatch().getTeamPlayerA().getPlayer() == p) {
                        if (profile.getMatch().getState() == MatchState.ENDING || profile.getMatch().getState() == MatchState.STARTING) {
                            p.sendMessage(CC.translate("&cYou cannot break bed right now."));
                            return;
                        }

                        p.sendMessage(CC.translate("&aYou broke the &9Blue Team&a's bed!"));
                        e.setCancelled(true);

                        profile.getMatch().getPlayers().forEach(player -> player.sendMessage(CC.translate("&6[MLG Rush] &9Blue&e's bed was destroyed by &c" + profile.getMatch().getTeamPlayerA().getUsername() + "&e.")));

                        profileA.setSumoRounds(profileA.getSumoRounds() + 1);

                        //kill the players
                        profile.getMatch().handleDeath(profileB.getPlayer(), profileA.getPlayer(), false);
                        //profile.getMatch().handleDeath(profileA.getPlayer(), profileB.getPlayer(), false);

                        profile.getMatch().getTeamPlayerB().getPlayer().setHealth(0);
                        profile.getMatch().getTeamPlayerB().getPlayer().spigot().respawn();

                        profile.getMatch().getTeamPlayerA().getPlayer().setHealth(0);
                        profile.getMatch().getTeamPlayerA().getPlayer().spigot().respawn();

                        if (profileA.getSumoRounds() == 3) {
                            profile.getMatch().onEnd();

                            profile.getMatch().getPlayers().forEach(player -> player.sendMessage(CC.translate("&6[MLG Rush] &cRed Team&e won the game!")));
                            profile.getMatch().getPlayers().forEach(player -> player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 5F, 5F));
                        } else {
                            profileA.getPlayer().teleport(profile.getMatch().getTeamPlayerA().getPlayerSpawn());
                            profileB.getPlayer().teleport(profile.getMatch().getTeamPlayerB().getPlayerSpawn());
                        }

                        for ( TeamPlayer teamPlayer : profile.getMatch().getTeamPlayers() ) {
                            teamPlayer.getPlayer().teleport(teamPlayer.getPlayerSpawn());
                        }

                        profile.getMatch().onDeath(profile.getMatch().getTeamPlayerB().getPlayer(), (Player) PlayerUtil.getLastDamager(profile.getPlayer()));


                    } else {
                        e.setCancelled(true);
                        p.sendMessage(CC.translate("&cYou cannot break your own bed."));
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 5F);
                    }
                }
                if (m == Material.BED_BLOCK && loc.getBlockX() == profile.getMatch().getArena().getBedLocationA().getBlockX() && loc.getBlockZ() == profile.getMatch().getArena().getBedLocationA().getBlockZ() ) {
                //if (m == Material.BED_BLOCK && loc.getWorld() == w2 && ((loc.getBlockX() == x2 && loc.getBlockY() == y2 && loc.getBlockZ() == z2) || (loc.getBlockX() == x2 + 1 && loc.getBlockY() == y2 && loc.getBlockZ() == z2) || (loc.getBlockX() == x2 - 1 && loc.getBlockY() == y2 && loc.getBlockZ() == z2) || (loc.getBlockX() == x2 && loc.getBlockY() == y2 && loc.getBlockZ() == z2 + 1) || (loc.getBlockX() == x2 && loc.getBlockY() == y2 && loc.getBlockZ() == z2 - 1))) {
                    if (profile.getMatch().getTeamPlayerB().getPlayer() == p) {
                        if (profile.getMatch().getState() == MatchState.ENDING || profile.getMatch().getState() == MatchState.STARTING) {
                            p.sendMessage(CC.translate("&cYou cannot break bed right now."));
                            return;
                        }

                        p.sendMessage(CC.translate("&aYou broke the &cRed Team&a's bed!"));
                        e.setCancelled(true);

                        profile.getMatch().getPlayers().forEach(player -> player.sendMessage(CC.translate("&6[MLG Rush] &cRed&e's bed was destroyed by &9" + profile.getMatch().getTeamPlayerB().getUsername() + "&e.")));

                        profileB.setSumoRounds(profileB.getSumoRounds() + 1);

                        //kill the players
                        //profile.getMatch().handleDeath(profileB.getPlayer(), profileA.getPlayer(), false);
                        profile.getMatch().handleDeath(profileA.getPlayer(), profileB.getPlayer(), false);

                        profile.getMatch().getTeamPlayerB().getPlayer().setHealth(0);
                        profile.getMatch().getTeamPlayerB().getPlayer().spigot().respawn();

                        profile.getMatch().getTeamPlayerA().getPlayer().setHealth(0);
                        profile.getMatch().getTeamPlayerA().getPlayer().spigot().respawn();


                        if (profileA.getSumoRounds() == 3) {
                            profile.getMatch().onEnd();

                            profile.getMatch().getPlayers().forEach(player -> player.sendMessage(CC.translate("&6[MLG Rush] &9Blue Team&e won the game!")));
                            profile.getMatch().getPlayers().forEach(player -> player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 5F, 5F));
                        } else {
                            profileA.getPlayer().teleport(profile.getMatch().getTeamPlayerA().getPlayerSpawn());
                            profileB.getPlayer().teleport(profile.getMatch().getTeamPlayerB().getPlayerSpawn());
                        }

                        for ( TeamPlayer teamPlayer : profile.getMatch().getTeamPlayers() ) {
                            teamPlayer.getPlayer().teleport(teamPlayer.getPlayerSpawn());
                        }

                        profile.getMatch().onDeath(profile.getMatch().getTeamPlayerA().getPlayer(), (Player) PlayerUtil.getLastDamager(profile.getPlayer()));

                    }
                    else {
                        e.setCancelled(true);
                        p.sendMessage(CC.translate("&cYou cannot break your own bed."));
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 5F);
                    }
                }
            }
        }
        catch (Exception ex) {}
    }

}
