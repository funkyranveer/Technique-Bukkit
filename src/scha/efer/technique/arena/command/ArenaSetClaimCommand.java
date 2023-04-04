package scha.efer.technique.arena.command;


import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.ArenaType;
import scha.efer.technique.arena.Claim;
import scha.efer.technique.arena.listeners.ArenaClaimListener;
import scha.efer.technique.match.team.TeamType;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ItemBuilder;

import java.util.Arrays;

@CommandMeta(label = "arena claim", permission = "technique.dev")
public class ArenaSetClaimCommand {
    public void execute(Player player, @CPL("arena") String arenaName, @CPL("[blue|red]") TeamType teamType) {
        if (teamType == null) {
            player.sendMessage(CC.translate("&cTeam not found!"));
            return;
        }

        Arena mainArena = Arena.getByName(arenaName);
        Arena arena = Arena.getByName(arenaName);

        if (mainArena == null) {
            player.sendMessage(CC.translate("&7An arena with that name does not exist."));
            return;
        }

        if (mainArena.getType() != ArenaType.THEBRIDGE) {
            player.sendMessage(CC.translate("&7That arena is not a &5TheBridge &7arena."));
            return;
        }

        if (ArenaClaimListener.makingClaim.containsKey(player.getUniqueId())) {
            Arena a = ArenaClaimListener.arenaClaimMap.get(player.getUniqueId());
            final Claim claim = ArenaClaimListener.makingClaim.get(player.getUniqueId());
            TeamType team = ArenaClaimListener.teamClaimMap.get(player.getUniqueId());

            if (!a.getName().equalsIgnoreCase(arena.getName())) {
                player.sendMessage(CC.translate("&cYou are already making a different arena claim!"));
                return;
            }

            if (claim.getCorner1() == null || claim.getCorner2() == null) {
                player.sendMessage(CC.translate("&cPlease finish the claim selection!"));
                return;
            }

            if (team == TeamType.TEAM_1) {
                mainArena.setBridgesRedClaim(new Claim(team, claim.getCorner1(), claim.getCorner2()));
            } else {
                mainArena.setBridgesBlueClaim(new Claim(team, claim.getCorner1(), claim.getCorner2()));
            }

            player.getInventory().remove(Material.DIAMOND_HOE);
            player.sendMessage(CC.translate((team == TeamType.TEAM_1 ? "&cRed" : "&9Blue") + " team's claim of arena &5" + arena.getName() + " &aset&f!"));

            ArenaClaimListener.arenaClaimMap.remove(player.getUniqueId());
            ArenaClaimListener.makingClaim.remove(player.getUniqueId());
            ArenaClaimListener.teamClaimMap.remove(player.getUniqueId());
            return;
        }

        // Didn't claim
        if(!giveClaimWand(player)) {
            player.sendMessage(CC.translate("&cPlease make space in your inventory!"));
            return;
        }

        Claim claim = new Claim(teamType, null, null);

        ArenaClaimListener.arenaClaimMap.put(player.getUniqueId(), arena);
        ArenaClaimListener.makingClaim.put(player.getUniqueId(), claim);
        ArenaClaimListener.teamClaimMap.put(player.getUniqueId(), teamType);

        player.sendMessage(CC.translate("&fSelect both bounds for the claim and then execute again &5/arena claim " + arena.getName() + " red/blue&f."));
        return;
    }

    private boolean giveClaimWand(Player player) {
        ItemStack claimWand = new ItemBuilder(Material.DIAMOND_HOE)
                .name("&5Claim Wand")
                .lore(Arrays.asList(
                "&7Left click to select the &5first &7position.",
                "&7Right click to select the &5second &7position."
        )).build();

        player.getInventory().addItem(claimWand);
        player.updateInventory();

        return true;
    }
}
