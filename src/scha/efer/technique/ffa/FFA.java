package scha.efer.technique.ffa;

import org.bukkit.entity.Player;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.util.external.CC;

public class FFA {

    public static void assignKit(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(null);

        Kit kit = Kit.getByName("NoDebuff");

        player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
        player.getInventory().setContents(kit.getKitLoadout().getContents());
        player.addPotionEffects(kit.getKitLoadout().getEffects());
        player.updateInventory();
    }

    public static void handleJoin(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_FFA);
        TechniquePlugin.get().getEssentials().teleportToSpawn(player);
        profile.refreshHotbar();

        player.sendMessage("   ");
        player.sendMessage(CC.YELLOW + CC.BOLD + "FFA");
        player.sendMessage(CC.translate("&f → &eUse &d/leave&e to leave."));
        player.sendMessage(CC.translate("&f → &eTeaming isn't allowed."));
        player.sendMessage("   ");

        FFA.assignKit(player);
    }

    public static void handleLeave(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.IN_LOBBY);
        TechniquePlugin.get().getEssentials().teleportToSpawn(player);
        player.getInventory().setContents(null);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        profile.refreshHotbar();
    }
}
