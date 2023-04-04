package scha.efer.technique.arena.listeners;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.Claim;
import scha.efer.technique.match.team.TeamType;
import scha.efer.technique.util.external.CC;


public class ArenaClaimListener implements Listener {

	public static Map<UUID, Arena> arenaClaimMap = new ConcurrentHashMap<>();
	public static Map<UUID, Claim> makingClaim = new ConcurrentHashMap<>();
	public static Map<UUID, TeamType> teamClaimMap = new ConcurrentHashMap<>();
	
	public ArenaClaimListener() {
		Bukkit.getPluginManager().registerEvents(this, TechniquePlugin.get());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		arenaClaimMap.remove(player.getUniqueId());
		makingClaim.remove(player.getUniqueId());
		teamClaimMap.remove(player.getUniqueId());
		player.getInventory().remove(Material.DIAMOND_HOE);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		arenaClaimMap.remove(player.getUniqueId());
		makingClaim.remove(player.getUniqueId());
		teamClaimMap.remove(player.getUniqueId());
		player.getInventory().remove(Material.DIAMOND_HOE);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Claim claim = makingClaim.get(player.getUniqueId());
		TeamType team = teamClaimMap.get(player.getUniqueId());
		
		if(claim == null || team == null) return;
		
		ItemStack item = event.getItem();
		
		if(item != null && item.getType() == Material.DIAMOND_HOE && item.hasItemMeta()) {
			Block block = event.getClickedBlock();
			
			if(block == null || block.getType() == Material.AIR) {
				player.sendMessage(CC.translate("&cYou can't select air!"));
				return;
			}
			
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				claim.setCorner1(block.getLocation());
				
				player.sendMessage(CC.translate("&fFirst position &asetted &fon &5" + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ() + "&f!"));
				return;
			} else {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				claim.setCorner2(block.getLocation());
				
				player.sendMessage(CC.translate("&fSecond position &asetted &fon &5" + block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " + block.getLocation().getBlockZ() + "&f!"));
				return;
			}
		}
	}
}
