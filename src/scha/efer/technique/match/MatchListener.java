package scha.efer.technique.match;

import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.impl.TheBridgeArena;
import scha.efer.technique.ffa.FFA;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.kit.KitLoadout;
import scha.efer.technique.match.impl.TheBridgeMatch;
import scha.efer.technique.match.team.Team;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.profile.hotbar.Hotbar;
import scha.efer.technique.profile.hotbar.HotbarItem;
import scha.efer.technique.util.BlockUtil;
import scha.efer.technique.util.LocationUtils;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import scha.efer.technique.util.external.TimeUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MatchListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlaceEvent(final BlockPlaceEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInFight()) {
            final Match match = profile.getMatch();
            if (!profile.getMatch().isHCFMatch()) {
                if (match.getKit().getGameRules().isBuild() && profile.getMatch().isFighting()) {
                    if (match.getKit().getGameRules().isSpleef()) {
                        event.setCancelled(true);
                        return;
                    }
                    final Arena arena = match.getArena();
                    final int y = (int) event.getBlockPlaced().getLocation().getY();
                    if (y > arena.getMaxBuildHeight()) {
                        event.getPlayer().sendMessage(CC.RED + "You have reached the maximum build height.");
                        event.setCancelled(true);
                        return;
                    }
                    if (arena instanceof TheBridgeArena) {
                        TheBridgeArena standaloneArena = (TheBridgeArena) arena;
                        if (standaloneArena.getBlueCuboid() != null && standaloneArena.getBlueCuboid().contains(event.getBlockPlaced())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (standaloneArena.getRedCuboid() != null && standaloneArena.getRedCuboid().contains(event.getBlockPlaced())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    match.getPlacedBlocks().add(event.getBlock().getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLow(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player);
        Match match = profile.getMatch();
        if (profile.isInFight()) {
            if (match.isTheBridgeMatch()) {
                if (player.getLocation().getBlockY() <= 30) {
                    player.setFallDistance(0);
                    player.setHealth(20.0);
                    player.teleport(match.getTeamPlayer(player).getPlayerSpawn());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreakEvent(final BlockBreakEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInFight()) {
            final Match match = profile.getMatch();
            if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                if (match.getKit().getGameRules().isBuild() && profile.getMatch().isFighting()) {
                    if (match.getKit().getGameRules().isSpleef()) {
                        if (event.getBlock().getType() == Material.SNOW_BLOCK || event.getBlock().getType() == Material.SNOW) {
                            match.getChangedBlocks().add(event.getBlock().getState());
                            event.getBlock().setType(Material.AIR);
                            event.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
                            event.getPlayer().updateInventory();
                        } else {
                            event.setCancelled(true);
                        }
                    } else if (!match.getPlacedBlocks().remove(event.getBlock().getLocation())) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketEmptyEvent(final PlayerBucketEmptyEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInFight()) {
            final Match match = profile.getMatch();
            if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                if (match.getKit().getGameRules().isBuild() && profile.getMatch().isFighting()) {
                    final Arena arena = match.getArena();
                    final Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                    final int x = (int) block.getLocation().getX();
                    final int y = (int) block.getLocation().getY();
                    final int z = (int) block.getLocation().getZ();
                    if (y > arena.getMaxBuildHeight()) {
                        event.getPlayer().sendMessage(CC.RED + "You have reached the maximum build height.");
                        event.setCancelled(true);
                        return;
                    }
                    match.getPlacedBlocks().add(block.getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockMove(final BlockFromToEvent event) {
        final int id = event.getBlock().getTypeId();
        if (id >= 8 && id <= 11) {
            final Block b = event.getToBlock();
            final int toid = b.getTypeId();
            if (toid == 0 && BlockUtil.generatesCobble(id, b)) {
                event.setCancelled(true);
            }
        }
        final Location l = event.getToBlock().getLocation();
        final List<UUID> playersinarena = new ArrayList<UUID>();
        for (final Entity entity : BlockUtil.getNearbyEntities(l, 50)) {
            if (entity instanceof Player) {
                playersinarena.add(((Player) entity).getPlayer().getUniqueId());
            }
        }
        if (playersinarena.size() > 0) {
            final Profile profile = Profile.getByUuid(playersinarena.get(0));
            if (profile.isInFight()) {
                final Match match = profile.getMatch();
                match.getPlacedBlocks().add(event.getToBlock().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupItemEvent(final PlayerPickupItemEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
            return;
        }
        if (profile.isInFight()) {
            if (!profile.getMatch().getTeamPlayer(event.getPlayer()).isAlive()) {
                event.setCancelled(true);
                return;
            }
            if (event.getItem().getItemStack().getType().name().contains("BOOK")) {
                event.setCancelled(true);
                return;
            }
            final Iterator<Entity> entityIterator = profile.getMatch().getEntities().iterator();
            while (entityIterator.hasNext()) {
                final Entity entity = entityIterator.next();
                if (entity instanceof Item && entity.equals(event.getItem())) {
                    entityIterator.remove();
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
        if (event.getItemDrop().getItemStack().getType() == Material.BOOK || event.getItemDrop().getItemStack().getType() == Material.ENCHANTED_BOOK) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().getType() == Material.INK_SACK) {
            event.getItemDrop().remove();
            return;
        }
        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.DIAMOND_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.BARD_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.ARCHER_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.ROGUE_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (profile.isInSomeSortOfFight()) {
            if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
                event.getItemDrop().setTicksLived(5940);
                return;
            }
            if (profile.getMatch() != null) {
                profile.getMatch().getEntities().add(event.getItemDrop());
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(final ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.DIAMOND_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        } else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.BARD_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        } else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.ARCHER_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        } else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.ROGUE_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(final PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity().getPlayer();
        final Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
        if (profile.isInFight()) {
            if (profile.getMatch().isTheBridgeMatch()) {
                TheBridgeMatch bridgeMatch = (TheBridgeMatch) profile.getMatch();
                event.getDrops().clear();

                PlayerUtil.reset(player);
                TheBridgeMatch.giveBridgeKit(player);
                player.updateInventory();

                player.teleport(bridgeMatch.getTeamPlayer(player).getPlayerSpawn());
                return;
            }
        }
        if (profile.isInFight()) {
            event.getDrops().clear();
            if (PlayerUtil.getLastDamager(event.getEntity()) instanceof CraftPlayer) {
                final Player killer = (Player) PlayerUtil.getLastDamager(event.getEntity());
                profile.getMatch().handleDeath(event.getEntity(), killer, false);
            } else {
                profile.getMatch().handleDeath(event.getEntity(), event.getEntity().getKiller(), false);
            }
        }
        if (profile.isInFFA()) {
            event.getDrops().clear();
            player.spigot().respawn();
            FFA.handleJoin(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        event.setRespawnLocation(event.getPlayer().getLocation());
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInFight()) {
            profile.getMatch().handleRespawn(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunchEvent(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof ThrownPotion && event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player) event.getEntity().getShooter();
            final Profile shooterData = Profile.getByUuid(shooter.getUniqueId());
            if (shooterData.isInFight() && shooterData.getMatch().isFighting()) {
                shooterData.getMatch().getTeamPlayer(shooter).incrementPotionsThrown();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHitEvent(final ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player) event.getEntity().getShooter();
            final Profile shooterData = Profile.getByUuid(shooter.getUniqueId());
            if (shooterData.isInFight()) {
                shooterData.getMatch().getEntities().add(event.getEntity());
                shooterData.getMatch().getTeamPlayer(shooter).handleHit();

            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplashEvent(final PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            final Player shooter = (Player) event.getPotion().getShooter();
            final Profile shooterData = Profile.getByUuid(shooter.getUniqueId());
            if (shooterData.isSpectating()) {
                event.setCancelled(true);
            }
            if (shooterData.isInFight() && event.getIntensity(shooter) <= 0.5) {
                shooterData.getMatch().getTeamPlayer(shooter).incrementPotionsMissed();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player && event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            final Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
            if (profile.isInFight() && !profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch() && !profile.getMatch().getKit().getGameRules().isHealthRegeneration()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEnterEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile=Profile.getByUuid(player.getUniqueId());
            if (profile.getState() == ProfileState.IN_FIGHT) {
                if (profile.getMatch().getKit().getGameRules().isBridge()) {
                    if (player.getLocation().getBlock().getType() == Material.ENDER_PORTAL ||
                            player.getLocation().getBlock().getType() == Material.ENDER_PORTAL_FRAME) {
                        if (LocationUtils.isTeamPortal(player)) {
                            player.teleport(profile.getMatch().getTeamPlayer(player).getPlayerSpawn());
                            player.sendMessage(CC.translate("&cYou jumped in the wrong portal."));
                            return;
                        }
                        TheBridgeMatch match=(TheBridgeMatch) profile.getMatch();
                        if (match.getState() == MatchState.ENDING) return;
                        for ( TeamPlayer teamPlayer : match.getTeamPlayers() ) {
                            Player other=teamPlayer.getPlayer();
                            other.sendMessage(match.getRelationColor(other, player) + player.getDisplayName() + CC.YELLOW + " scored a goal!");
                            teamPlayer.getPlayer().teleport(teamPlayer.getPlayerSpawn());
                            profile.setBridgeRounds(profile.getBridgeRounds() + 1);
                        }
                        TeamPlayer guy=match.getPlayerA().getPlayer() == player ? match.getTeamPlayerB() : match.getTeamPlayerA();
                        match.onDeath(guy.getPlayer(), (Player) PlayerUtil.getLastDamager(guy.getPlayer()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onShoot(final ProjectileLaunchEvent projectileLaunchEvent) {
        if (!(projectileLaunchEvent.getEntity() instanceof Arrow)) {
            return;
        }
        final Arrow arrow = (Arrow)projectileLaunchEvent.getEntity();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }
        final Player key = (Player)arrow.getShooter();
        Profile profile=Profile.getByUuid(key.getUniqueId());
        TheBridgeMatch match=(TheBridgeMatch) profile.getMatch();
        if (profile.getState() != ProfileState.IN_FIGHT) {
            return;
        }
        if (profile.getPlayerArrowTask().containsKey(key)) {
            return;
        }
        profile.getPlayerArrowTask().put(key, new BukkitRunnable() {
            int seconds = 4;

            public void run() {
                if (this.seconds == 0 && match.getState() == MatchState.FIGHTING) {
                    key.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
                    key.playSound(key.getLocation(), Sound.ITEM_PICKUP, 5F, 5F);
                    key.setLevel(0);
                    profile.getPlayerArrowTask().remove(key);
                    this.cancel();
                    return;
                }
                key.setLevel(this.seconds);
                --this.seconds;
            }
        }.runTaskTimer(TechniquePlugin.get(), 0L, 20L));
    }


    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInFight()) {
                final Match match = profile.getMatch();
                Location spawn = match.getTeamPlayerA().equals(match.getTeamPlayer(player)) ? match.getArena().getSpawn1() : match.getArena().getSpawn2();
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    if (profile.getMatch().getKit().getGameRules().isVoidspawn() || profile.getMatch().getKit().getName().equalsIgnoreCase("MLG-Rush") || profile.getMatch().isTheBridgeMatch() || profile.getMatch().getKit().getGameRules().isBridge()) {
                        event.setDamage(0.0);
                        player.setFallDistance(0);
                        player.setHealth(20.0);
                        player.teleport(spawn);
                        player.sendMessage(CC.translate("&aYou have been teleported to your spawn."));
                        if (profile.getMatch().getKit().getName().equalsIgnoreCase("The-Bridge") || profile.getMatch().isTheBridgeMatch()) {
                            TheBridgeMatch bridgeMatch = (TheBridgeMatch) match;
                            PlayerUtil.reset(player);
                            bridgeMatch.setupPlayer(player);
                            player.updateInventory();
                        }
                        return;
                    }
                    profile.getMatch().handleDeath(player, null, false);
                    return;
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL && profile.getMatch().getKit().getName().equalsIgnoreCase("The-Bridge")) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.LAVA && !profile.getMatch().isHCFMatch()  && profile.getMatch().getKit().getGameRules().isLavakill()) {
                    profile.getMatch().handleDeath(player, null, false);
                    return;
                }

                if (!profile.getMatch().isHCFMatch()  && profile.getMatch().getKit().getGameRules().isParkour()) {
                    event.setCancelled(true);
                    return;
                }
                if (!profile.getMatch().isFighting()) {
                    event.setCancelled(true);
                    return;
                }

                if ((profile.getMatch().isTeamMatch() || profile.getMatch().isHCFMatch()) && !profile.getMatch().getTeamPlayer(player).isAlive()) {
                    event.setCancelled(true);
                    return;
                }
                if (!profile.getMatch().isHCFMatch()  && profile.getMatch().getKit().getGameRules().isSumo() || profile.getMatch().getKit().getGameRules().isBoxing() || profile.getMatch().getKit().getName().equalsIgnoreCase("Stick-Fight")) {
                    event.setDamage(0.0);
                    player.setHealth(20.0);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        Player attacker;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else {
            if (!(event.getDamager() instanceof Projectile)) {
                event.setCancelled(true);
                return;
            }
            if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                event.setCancelled(true);
                return;
            }
            attacker = (Player) ((Projectile) event.getDamager()).getShooter();
        }
        if (attacker != null && event.getEntity() instanceof Player) {
            final Player damaged = (Player) event.getEntity();
            final Profile damagedProfile = Profile.getByUuid(damaged.getUniqueId());
            final Profile attackerProfile = Profile.getByUuid(attacker.getUniqueId());
            if (attackerProfile.isSpectating() || damagedProfile.isSpectating()) {
                event.setCancelled(true);
                return;
            }
            if (damagedProfile.isInFight() && attackerProfile.isInFight()) {
                final Match match = attackerProfile.getMatch();
                if (!match.isHCFMatch() && !match.isKoTHMatch() && match.getKit().getGameRules().isSpleef() && !(event.getDamager() instanceof Projectile)) {
                    event.setCancelled(true);
                }
                if (!damagedProfile.getMatch().isHCFMatch() && !damagedProfile.getMatch().isKoTHMatch() && damagedProfile.getMatch().getKit().getGameRules().isSpleef() && !(event.getDamager() instanceof Projectile)) {
                    event.setCancelled(true);
                    return;
                }
                if (!damagedProfile.getMatch().getMatchId().equals(attackerProfile.getMatch().getMatchId())) {
                    event.setCancelled(true);
                    return;
                }
                if (!match.getTeamPlayer(damaged).isAlive() || (!match.getTeamPlayer(attacker).isAlive() && !match.isFreeForAllMatch())) {
                    event.setCancelled(true);
                    return;
                }
                if (match.isSoloMatch() || match.isFreeForAllMatch() || match.isSumoMatch() || match.isTheBridgeMatch() || match.isBoxingMatch()) {
                    attackerProfile.getMatch().getTeamPlayer(attacker).handleHit();
                    damagedProfile.getMatch().getTeamPlayer(damaged).resetCombo();
                    if (match.getKit().getGameRules().isBoxing()) {
                        if (attackerProfile.getMatch().getTeamPlayer(attacker).getHits() == 100) {
                            damaged.setHealth(0);
                            damaged.spigot().respawn();
                            attacker.sendMessage(CC.translate("&3[Boxing] &d" + attacker.getName() + "&e has reached &d100&e hits! &a(Respect)"));
                            damaged.sendMessage(CC.translate("&3[Boxing] &d" + attacker.getName() + "&e has reached &d100&e hits! &a(Respect)"));
                        } else if (attackerProfile.getMatch().getTeamPlayer(attacker).getHits() == 50) {
                            attacker.sendMessage(CC.translate("&3[Boxing] &d" + attacker.getName() + "&e has reached &d50&e hits! &a(Respect)"));
                            damaged.sendMessage(CC.translate("&3[Boxing] &d" + attacker.getName() + "&e has reached &d50&e hits! &a(Respect)"));
                        }
                    }
                    if (event.getDamager() instanceof Arrow) {
                        final double range = Math.ceil(event.getEntity().getLocation().distance(attacker.getLocation()));
                        final double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0;
                        if (match.getKit().getGameRules().isBowhp()) {
                            if (!attacker.getName().equalsIgnoreCase(damaged.getName())) {
                                attacker.sendMessage(CC.translate("&5" + damaged.getName() + " &fis now at &5" + health + " &5" + StringEscapeUtils.unescapeJava("\u2764")));
                            }
                        }
                    }
                } else if (match.isTeamMatch() || match.isHCFMatch() || match.isKoTHMatch()) {
                    final Team attackerTeam = match.getTeam(attacker);
                    final Team damagedTeam = match.getTeam(damaged);
                    if (attackerTeam == null || damagedTeam == null) {
                        event.setCancelled(true);
                    } else if (attackerTeam.equals(damagedTeam)) {
                        if (!damaged.getUniqueId().equals(attacker.getUniqueId())) {
                            event.setCancelled(true);
                        }
                    } else {
                        attackerProfile.getMatch().getTeamPlayer(attacker).handleHit();
                        damagedProfile.getMatch().getTeamPlayer(damaged).resetCombo();
                        if (match.getKit().getGameRules().isBoxing()) {
                            if (attackerProfile.getMatch().getTeamPlayer(attacker).getHits() == 100) {
                                damaged.setHealth(0);
                                damaged.spigot().respawn();
                                attacker.sendMessage(CC.translate("&3[Boxing] &d" + attacker.getName() + "&e has reached &d100&e hits! &a(Respect)"));
                                damaged.sendMessage(CC.translate("&3[Boxing] &d" + attacker.getName() + "&e has reached &d100&e hits! &a(Respect)"));
                           } else if (attackerProfile.getMatch().getTeamPlayer(attacker).getHits() == 50) {
                                attacker.sendMessage(CC.translate("&3[Boxing] &d" + attacker.getName() + "&e has reached &d50&e hits! &a(Respect)"));
                                damaged.sendMessage(CC.translate("&3[Boxing] &d" + attacker.getName() + "&e has reached &d50&e hits! &a(Respect)"));
                            }
                        }
                        if (event.getDamager() instanceof Arrow) {
                            final double range2 = Math.ceil(event.getEntity().getLocation().distance(attacker.getLocation()));
                            final double health2 = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0;
                            if (match.getKit() == null || match.getKit().getGameRules().isBowhp()) {
                                if (!attacker.getName().equalsIgnoreCase(damaged.getName())) {
                                    attacker.sendMessage(CC.translate("&d" + damaged.getName() + " &eis now at &5" + health2 + " &4" + StringEscapeUtils.unescapeJava("\u2764")));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(final PlayerItemConsumeEvent event) {
        if (event.getItem().getType().equals(Material.POTION)) {
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(TechniquePlugin.get(), new Runnable() {
                @Override
                public void run() {
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                }
            }, 1L);
        }
        if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
            final Player player = event.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
            player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
        }
    }

    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInFight() && !profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch() && profile.getMatch() != null && profile.getMatch().getKit().getGameRules().isAntifoodloss()) {
                event.setFoodLevel(20);
            }
            if (profile.isInFight() && profile.getMatch().isFighting()) {
                if (event.getFoodLevel() >= 20) {
                    event.setFoodLevel(20);
                    player.setSaturation(20.0f);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());
        if (profile.isInFight()) {
            profile.getMatch().handleDeath(event.getPlayer(), null, true);
        } else if (profile.isInMatch()) {
            profile.getMatch().handleDeath(event.getPlayer(), null, true);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryInteract(final InventoryInteractEvent event) {
        final Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            final EnderPearl enderPearl = (EnderPearl) projectile;
            final ProjectileSource source = enderPearl.getShooter();
            if (source instanceof Player) {
                final Player shooter = (Player) source;
                final Profile profile = Profile.getByUuid(shooter.getUniqueId());
                if (profile.isInFight()) {
                    if (!profile.getEnderpearlCooldown().hasExpired()) {
                        final String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
                        final String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");
                        shooter.sendMessage(CC.RED + "You are on pearl cooldown for " + time + " " + context);
                        shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        event.setCancelled(true);
                    } else {
                        profile.setEnderpearlCooldown(new Cooldown(16000L));
                        profile.getMatch().onPearl(shooter, enderPearl);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleportPearl(final PlayerTeleportEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && profile.isInFight()) {
            profile.getMatch().removePearl(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
        if (event.getItem() != null && event.getAction().name().contains("RIGHT") && profile.isInFight()) {
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.DEFAULT_KIT))) {
                    final KitLoadout kitLoadout = profile.getMatch().getKit().getKitLoadout();
                    event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                    event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                    event.getPlayer().getActivePotionEffects().clear();
                    if (profile.getMatch().getKit().getKitLoadout().getEffects() != null) {
                        event.getPlayer().addPotionEffects(profile.getMatch().getKit().getKitLoadout().getEffects());
                    }
                    event.getPlayer().updateInventory();
                    event.setCancelled(true);
                    return;
                }
                if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.DIAMOND_KIT))) {
                    final KitLoadout kitLoadout = Kit.getByName("HCFDIAMOND").getKitLoadout();
                    event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                    event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                    event.getPlayer().getActivePotionEffects().clear();
                    if (kitLoadout.getEffects() != null) {
                        event.getPlayer().addPotionEffects(kitLoadout.getEffects());
                    }
                    event.getPlayer().updateInventory();
                    TechniquePlugin.get().getArmorClassManager().attemptEquip(event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
                if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.BARD_KIT))) {
                    final KitLoadout kitLoadout = Kit.getByName("HCFBARD").getKitLoadout();
                    event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                    event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                    event.getPlayer().getActivePotionEffects().clear();
                    if (kitLoadout.getEffects() != null) {
                        event.getPlayer().addPotionEffects(kitLoadout.getEffects());
                    }
                    event.getPlayer().updateInventory();
                    TechniquePlugin.get().getArmorClassManager().attemptEquip(event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
                if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.ARCHER_KIT))) {
                    final KitLoadout kitLoadout = Kit.getByName("HCFARCHER").getKitLoadout();
                    event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                    event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                    event.getPlayer().getActivePotionEffects().clear();
                    if (kitLoadout.getEffects() != null) {
                        event.getPlayer().addPotionEffects(kitLoadout.getEffects());
                    }
                    event.getPlayer().updateInventory();
                    TechniquePlugin.get().getArmorClassManager().attemptEquip(event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
                if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.ROGUE_KIT))) {
                    final KitLoadout kitLoadout = Kit.getByName("HCFROGUE").getKitLoadout();
                    event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                    event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                    event.getPlayer().getActivePotionEffects().clear();
                    if (kitLoadout.getEffects() != null) {
                        event.getPlayer().addPotionEffects(kitLoadout.getEffects());
                    }
                    event.getPlayer().updateInventory();
                    TechniquePlugin.get().getArmorClassManager().attemptEquip(event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
            }
            if (!profile.getMatch().isHCFMatch() && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().hasLore()) {
                final String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
                if (displayName.endsWith(" (Right-Click)")) {
                    final String kitName = displayName.replace(" (Right-Click)", "");
                    for (final KitLoadout kitLoadout2 : profile.getKitData().get(profile.getMatch().getKit()).getLoadouts()) {
                        if (kitLoadout2 != null && ChatColor.stripColor(kitLoadout2.getCustomName()).equals(kitName)) {
                            event.getPlayer().getInventory().setArmorContents(kitLoadout2.getArmor());
                            event.getPlayer().getInventory().setContents(kitLoadout2.getContents());
                            event.getPlayer().getActivePotionEffects().clear();
                            event.getPlayer().addPotionEffects(profile.getMatch().getKit().getKitLoadout().getEffects());
                            event.getPlayer().updateInventory();
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }

            final Player player = event.getPlayer();
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && player.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
                final int health = (int) player.getHealth();
                if (health == 20) {
                    player.getItemInHand().setType(Material.MUSHROOM_SOUP);
                } else if (health >= 13) {
                    player.setHealth(20.0);
                    player.getItemInHand().setType(Material.BOWL);
                } else {
                    player.setHealth(health + 7);
                    player.getItemInHand().setType(Material.BOWL);
                }
            }
            if ((event.getItem().getType() == Material.ENDER_PEARL || (event.getItem().getType() == Material.POTION && event.getItem().getDurability() >= 16000)) && profile.isInFight() && profile.getMatch().isStarting()) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
            if (event.getItem().getType() == Material.ENDER_PEARL && event.getClickedBlock() == null) {
                if (!profile.isInFight() || (profile.isInFight() && !profile.getMatch().isFighting())) {
                    event.setCancelled(true);
                    return;
                }
                if (profile.getMatch().isStarting()) {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onPressurePlate(final PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL) && e.getClickedBlock().getType() == Material.GOLD_PLATE) {
            final Profile profile = Profile.getByUuid(e.getPlayer().getUniqueId());
            if (profile.isInFight() && !profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch() && profile.getMatch().getKit().getGameRules().isParkour()) {
                profile.getMatch().handleDeath(e.getPlayer(), null, false);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent e) {
        if (e.isCancelled() || e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        final Location target = e.getTo();
        target.setX(target.getBlockX() + 0.5);
        target.setZ(target.getBlockZ() + 0.5);
        e.setTo(target);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getMatch() != null) {
            Match match=profile.getMatch();
            if (profile.getMatch().getKit() != null) {
                if (profile.getMatch().isSumoMatch() || profile.getMatch().isSumoTeamMatch() || profile.getMatch().getKit().getGameRules().isStickspawn()
                        || profile.getMatch().getKit().getGameRules().isSumo() || profile.getMatch().isTheBridgeMatch() || profile.getMatch().getKit().getGameRules().isBridge()) {
                    if (match.getState() == MatchState.STARTING) {
                        Location from=event.getFrom();
                        Location to=event.getTo();
                        if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
                            player.teleport(from);
                            ((CraftPlayer) player).getHandle().playerConnection.checkMovement=false;
                        }
                    }
                }
                //Just Drizzy messing around with MetaData
            } else if (player.hasMetadata("ArrayTest")) {
                event.getPlayer().teleport(event.getPlayer().getLocation());
            }
        }
    }

    @EventHandler
    public void onKothMove(final PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            final Profile profile = Profile.getByUuid(e.getPlayer().getUniqueId());
            if (profile.getMatch() != null && profile.getMatch().isKoTHMatch()) {
                final Match match = profile.getMatch();
                if (match.getArena().getPoint().toCuboid() == null) {
                    return;
                }
                if (match.getArena().getPoint().toCuboid().isInCuboid(e.getPlayer())) {
                    if (match.getCapper() == null && !profile.isSpectating()) {
                        match.setCapper(e.getPlayer());
                    }
                } else if (match.getCapper() != null && match.getCapper().equals(e.getPlayer())) {
                    match.setCapper(null);
                    match.setTimer(20);
                }
            }
        }
    }

    @EventHandler
    public void onKothDeath(final PlayerDeathEvent e) {
        final Profile profile = Profile.getByUuid(e.getEntity().getUniqueId());
        if (profile.getMatch() != null && profile.getMatch().isKoTHMatch()) {
            final Match match = profile.getMatch();
            if (match.getArena().getPoint().toCuboid() == null) {
                return;
            }
            if (match.getCapper() != null && match.getCapper().equals(e.getEntity().getPlayer())) {
                match.setCapper(null);
                match.setTimer(20);
            }
        }
    }

    @EventHandler
    public void onPotionSplash(final PotionSplashEvent event) {
        final Iterator<LivingEntity> iterator = event.getAffectedEntities().iterator();
        while (iterator.hasNext()) {
            final LivingEntity entity = iterator.next();
            if (entity instanceof Player) {
                final Player player = (Player) entity;
                final Profile profile = Profile.getByUuid(player.getUniqueId());
                if (!profile.isSpectating()) {
                    continue;
                }
                event.setIntensity(player, 0.0);
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        final Profile profile = Profile.getByUuid(e.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            profile.getMatch().removeSpectator(e.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageEntity(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player damaged = (Player) e.getEntity();
            Profile damagedProfile = Profile.getByUuid(damaged.getUniqueId());

            if (damagedProfile.getMatch() != null) {
                Match match = damagedProfile.getMatch();
                if (match.isEnding()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onProjLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player player = (Player) e.getEntity().getShooter();
        Profile profile = Profile.getByUuid(player);
        Match match = profile.isInMatch() ? profile.getMatch() : null;

        if (match == null)
            return;

        Projectile entity = e.getEntity();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (match.getPlayersAndSpectators().contains(p)) continue;
            TechniquePlugin.get().getEntityHider().hideEntity(p, entity);
        }

    }

}
