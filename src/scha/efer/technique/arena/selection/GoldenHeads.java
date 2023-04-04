package scha.efer.technique.arena.selection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import scha.efer.technique.TechniquePlugin;

import java.util.HashMap;
import java.util.UUID;

public class GoldenHeads implements Listener {

	private static HashMap<UUID, Integer> lastCPS = new HashMap<UUID, Integer>();

	private static final String time = "CPSTimestamp";
	private static final String tag = "CPSCount";

	private TechniquePlugin plugin;

	public GoldenHeads(TechniquePlugin plugin) {
		this.plugin = plugin;
		new BukkitRunnable() {

			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					MetadataValue m = plugin.getMetadata(p, tag);
					if(m != null && m.value() != null) {
						MetadataValue m2 = plugin.getMetadata(p, time);
						if(m2 != null && m2.value() != null) {
							int cps = m.asInt();
							if(m2.asLong() < System.currentTimeMillis()) {
								lastCPS.put(p.getUniqueId(), 0);
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 10, 20);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.LEFT_CLICK_AIR)) {
			if(p.getItemInHand() != null && p.getItemInHand().getType() == Material.FISHING_ROD) return;
			if(p.hasMetadata(tag) && p.hasMetadata(time)) {
				MetadataValue m = plugin.getMetadata(p, tag);
				if(m != null && m.value() != null) {
					MetadataValue m2 = plugin.getMetadata(p, time);
					if(m2 != null && m2.value() != null) {
						int cps = m.asInt();
						if(m2.asLong() <= System.currentTimeMillis()) {
							lastCPS.put(p.getUniqueId(), cps);
							p.removeMetadata(tag, plugin);
							p.removeMetadata(time, plugin);
						}
						else p.setMetadata(tag, new FixedMetadataValue(plugin, cps+1));
						return;
					}
				}
			}
			p.setMetadata(tag, new FixedMetadataValue(plugin, 1));
			p.setMetadata(time, new FixedMetadataValue(plugin, System.currentTimeMillis()+1000));
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.getPlayer().removeMetadata(time, plugin);
		e.getPlayer().removeMetadata(tag, plugin);
	}

	public static int getCPS(Player p) {
		if(!Bukkit.isPrimaryThread()) {
			synchronized(lastCPS) {
				return lastCPS.getOrDefault(p.getUniqueId(), 0);
			}
		}
		return lastCPS.getOrDefault(p.getUniqueId(), 0);
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event){
		Player player = event.getPlayer();
		if (event.getItem() == null || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) return;
		if(event.getItem().getItemMeta().getDisplayName().toLowerCase().replace(" ", "").contains("goldenhead")) {
			if (event.getItem().getType() == Material.GOLDEN_APPLE){
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*90, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*9, 1), true);
			}
		}
		if(event.getItem().getItemMeta().getDisplayName().toLowerCase().replace(" ", "").replace("'", "").contains("schaefersapple")) {
			if (event.getItem().getType() == Material.GOLDEN_APPLE){
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*90, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*9, 0), true);
				player.setHealth(20.0D);
				player.setFoodLevel(20);
				player.setLevel(0);
				player.setExp(0f);
				player.setFireTicks(0);
			}
		}

	}
	
	public static ItemStack goldenHeadItem() {
			ItemStack is = new ItemStack(Material.GOLDEN_APPLE);
			ItemMeta itemMeta = is.getItemMeta();
			itemMeta.setDisplayName(ChatColor.GOLD + "Golden Head");
			is.setItemMeta(itemMeta);
			return is;
	}

	public static ItemStack getBridgeApple() {
		ItemStack is = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta itemMeta = is.getItemMeta();
		itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Schaefer's Apple");
		is.setItemMeta(itemMeta);
		return is;
	}

}
