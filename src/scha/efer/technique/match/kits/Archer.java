package scha.efer.technique.match.kits;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.match.kits.utils.ArmorClass;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Archer extends ArmorClass implements Listener {

    public static PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
    public static PotionEffect ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 7);
    public static Map<UUID, Long> archerSpeedCooldowns = new HashMap<>();
    public static Map<UUID, Long> archerJumpCooldowns = new HashMap<>();

    public static HashMap<UUID, UUID> TAGGED = new HashMap<UUID, UUID>();
    public static Random random = new Random();

    public static long ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45L);
    public static long ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);

    public TechniquePlugin plugin;

    public Archer(TechniquePlugin plugin) {
        super("Archer", TimeUnit.MILLISECONDS.toMillis(1L));
        this.plugin = plugin;

        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (((entity instanceof Player)) && ((damager instanceof Arrow))) {

            Arrow arrow = (Arrow) damager;
            ProjectileSource source = arrow.getShooter();

            if ((source instanceof Player)) {

                Player damaged = (Player) event.getEntity();
                Player shooter = (Player) source;

                if (!Profile.getByUuid(damaged.getUniqueId()).getMatch().isHCFMatch()) {
                    return;
                }

                ArmorClass equipped = this.plugin.getArmorClassManager().getEquippedClass(shooter);

                if ((equipped == null) || (!equipped.equals(this))) {
                    return;
                }
                if (true) {
                    if ((this.plugin.getArmorClassManager().getEquippedClass(damaged) != null) && (this.plugin.getArmorClassManager().getEquippedClass(damaged).equals(this))) {
                        return;
                    }

                    int heartdamage = 1;
                    if (TAGGED.containsKey(damaged.getUniqueId())) {
                        heartdamage = 2;
                    }

                    event.setDamage(0);

                    damaged.setHealth(Math.max(0, damaged.getHealth() - (heartdamage * 2)));

                    TAGGED.put(damaged.getUniqueId(), shooter.getUniqueId());
                    double distance = shooter.getLocation().distance(damaged.getLocation());

                    shooter.sendMessage(CC.translate("&5Range: " + String.format("%.1f", distance)));
                    shooter.sendMessage(CC.translate("&fMarked &5" + damaged.getName() + " &ffor &510 seconds &5" + heartdamage + "&5 ❤"));

                    damaged.sendMessage(CC.translate("&5Marked! &f" + shooter.getName() + " has &5shot &fyou and &5marked &fyou (+25% damage) for &510 seconds&f. &5(" + String.format("%.1f", distance) + " blocks away)"));

                    LeatherArmorMeta helmMeta = (LeatherArmorMeta) shooter.getInventory().getHelmet().getItemMeta();
                    LeatherArmorMeta chestMeta = (LeatherArmorMeta) shooter.getInventory().getChestplate().getItemMeta();
                    LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) shooter.getInventory().getLeggings().getItemMeta();
                    LeatherArmorMeta bootsMeta = (LeatherArmorMeta) shooter.getInventory().getBoots().getItemMeta();

                    Color green = Color.fromRGB(6717235);

                    double r = random.nextDouble();

                    if ((r <= 0.5D) && (helmMeta.getColor().equals(green)) && (chestMeta.getColor().equals(green)) && (leggingsMeta.getColor().equals(green)) && (bootsMeta.getColor().equals(green))) {
                        damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
                        shooter.sendMessage(ChatColor.WHITE + "Since your armor is green, you gave " + damaged.getName() + " the poison effect for 6 seconds...");
                        damaged.sendMessage(ChatColor.WHITE + "Since " + shooter.getName() + "'s armor is green, you were given the poison effect for 6 seconds...");
                    }
                    Color blue = Color.fromRGB(3361970);
                    if ((r <= 0.5D) && (helmMeta.getColor().equals(blue)) && (chestMeta.getColor().equals(blue)) && (leggingsMeta.getColor().equals(blue)) && (bootsMeta.getColor().equals(blue))) {
                        damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 0));
                        shooter.sendMessage(ChatColor.WHITE + "Since your armor is blue, you gave " + damaged.getName() + " the slowness effect for 6 seconds...");
                        damaged.sendMessage(ChatColor.WHITE + "Since " + shooter.getName() + "'s armor is blue, you were given the slowness effect for 6 seconds...");
                    }
                    Color gray = Color.fromRGB(5000268);
                    if ((r <= 0.5D) && (helmMeta.getColor().equals(gray)) && (chestMeta.getColor().equals(gray)) && (leggingsMeta.getColor().equals(gray)) && (bootsMeta.getColor().equals(gray))) {
                        damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
                        shooter.sendMessage(ChatColor.WHITE + "Since your armor is gray, you gave " + damaged.getName() + " the blindness effect for 6 seconds...");
                        damaged.sendMessage(ChatColor.WHITE + "Since " + shooter.getName() + "'s armor is gray, you were given the blindness effect for 6 seconds...");
                    }
                    Color black = Color.fromRGB(1644825);
                    if ((r <= 0.2D) && (helmMeta.getColor().equals(black)) && (chestMeta.getColor().equals(black)) && (leggingsMeta.getColor().equals(black)) && (bootsMeta.getColor().equals(black))) {
                        damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 0));
                        shooter.sendMessage(ChatColor.WHITE + "Since your armor is black, you gave " + damaged.getName() + " the wither effect for 6 seconds...");
                        damaged.sendMessage(ChatColor.WHITE + "Since " + shooter.getName() + "'s armor is black, you were given the wither effect for 6 seconds...");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Action action = event.getAction();

        if (Profile.getByUuid(uuid).getMatch() != null && !Profile.getByUuid(uuid).getMatch().isHCFMatch()) {
            return;
        }

        if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem()) && (event.getItem().getType() == Material.SUGAR)) {
            if (this.plugin.getArmorClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            long timestamp = archerSpeedCooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L);
            long millis = System.currentTimeMillis();
            long remaining = timestamp - millis;

            if (remaining > 0L) {
                player.sendMessage(ChatColor.DARK_PURPLE + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
            } else {
                ItemStack stack = player.getItemInHand();

                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                }

                this.plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_SPEED_EFFECT);
                archerSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_SPEED_COOLDOWN_DELAY);
            }
        } else if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem()) && (event.getItem().getType() == Material.FEATHER)) {

            if (this.plugin.getArmorClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }

            long timestamp = archerJumpCooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L);
            long millis = System.currentTimeMillis();
            long remaining1 = timestamp - millis;

            if (remaining1 > 0L) {
                player.sendMessage(ChatColor.DARK_PURPLE + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining1, true, true) + ".");
            } else {
                ItemStack stack = player.getItemInHand();

                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                }

                this.plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_JUMP_EFFECT);
                archerJumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_JUMP_COOLDOWN_DELAY);
            }
        }
    }

    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack helmet = playerInventory.getHelmet();
        if ((helmet == null) || (helmet.getType() != Material.LEATHER_HELMET)) {
            return false;
        }
        ItemStack chestplate = playerInventory.getChestplate();
        if ((chestplate == null) || (chestplate.getType() != Material.LEATHER_CHESTPLATE)) {
            return false;
        }
        ItemStack leggings = playerInventory.getLeggings();
        if ((leggings == null) || (leggings.getType() != Material.LEATHER_LEGGINGS)) {
            return false;
        }
        ItemStack boots = playerInventory.getBoots();
        return (boots != null) && (boots.getType() == Material.LEATHER_BOOTS);
    }

}