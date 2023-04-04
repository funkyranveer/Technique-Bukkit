package scha.efer.technique.commands;

import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.ItemBuilder;

@CommandMeta(label = {"refill", "more"}, permission = "technique.staff")
public class RefillCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player);
        if (profile.isInFight()) {
            if (player.getInventory().contains(Material.POTION)) {
                while (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(this.getPotion());
                }
            }
            if (player.getInventory().contains(Material.MUSHROOM_SOUP)) {
                while (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(this.getSoup());
                }
            }
        }
    }



    public ItemStack getPotion() {
        return new ItemBuilder(Material.POTION).durability(16421).build();
    }

    public ItemStack getSoup() {
        return new ItemBuilder(Material.MUSHROOM_SOUP).build();
    }
}
