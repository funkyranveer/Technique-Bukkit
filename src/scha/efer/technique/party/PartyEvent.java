package scha.efer.technique.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum PartyEvent {

    FFA("FFA", "Let your party members fight for themselves", Material.REDSTONE_TORCH_ON),
    SPLIT("Split", "Split your party in 2 teams and fight!", Material.DIAMOND_SWORD),
    HCF("HCF", "Split your party, and choose bard/diamond", Material.GOLD_CHESTPLATE),
    KOTH("KOTH", "Split your party, and cap a koth 5 times", Material.WOOL);

    private final String name;
    private final String lore;
    private final Material material;

}
