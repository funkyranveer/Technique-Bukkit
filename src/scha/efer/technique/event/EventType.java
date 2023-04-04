package scha.efer.technique.event;

import scha.efer.technique.TechniquePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum EventType {
    BRACKETS(TechniquePlugin.get().getBracketsManager().getActiveBrackets(), "Brackets", Material.IRON_SWORD),
    SUMO(TechniquePlugin.get().getSumoManager().getActiveSumo(), "Sumo", Material.LEASH),
    FFA(TechniquePlugin.get().getLMSManager().getActiveLMS(), "FFA", Material.REDSTONE),
    JUGGERNAUT(TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut(), "Juggernaut", Material.CHAINMAIL_CHESTPLATE),
    PARKOUR(TechniquePlugin.get().getParkourManager().getActiveParkour(), "Parkour", Material.FEATHER),
    WIPEOUT(TechniquePlugin.get().getWipeoutManager().getActiveWipeout(), "Wipeout", Material.WATER_BUCKET),
    SKYWARS(TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars(), "SkyWars", Material.DIAMOND_AXE),
    SPLEEF(TechniquePlugin.get().getSpleefManager().getActiveSpleef(), "Spleef", Material.DIAMOND_SPADE),
    INFECTED(TechniquePlugin.get().getInfectedManager().getActiveInfected(), "Infected", Material.SLIME_BALL);

    private final Object object;
    private final String title;
    private final Material material;
}
