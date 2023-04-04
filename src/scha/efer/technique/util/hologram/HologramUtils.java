package scha.efer.technique.util.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.util.TaskUtil;
import scha.efer.technique.util.external.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@Getter
public class HologramUtils {

    private final Location holoLoc;
    private Hologram hologram;

    public HologramUtils() {
        this.holoLoc = new Location(Bukkit.getWorld("world"), 0.500, 83, 8.500);
        TaskUtil.runSync(() -> HologramUtils.this.hologram = HologramsAPI.createHologram(TechniquePlugin.get(), holoLoc));


        createDefaultLadderHolo();

    }

    public void createDefaultLadderHolo() {
        TaskUtil.runAsync(() -> {
            if (!this.holoLoc.getChunk().isLoaded())
                holoLoc.getChunk().load();
        });

        TaskUtil.runSync(() -> {
            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine(CC.translate("&5&lNoDebuff Leaderboards"));
            hologram.appendTextLine(CC.translate("&f"));
            for (int i = 1; i <= 10; i++) {
                hologram.appendTextLine(CC.translate("%practice_lb_NoDebuff_" + i + "%"));
            }
        });
    }

    public void switchLadderHolo() {
        TaskUtil.runSync(() -> {
            String ladder = getNextLadder();

            hologram.clearLines();

            hologram.appendTextLine(CC.translate("&5&l" + ladder + " Leaderboards"));
            hologram.appendTextLine(CC.translate("&f"));
            for (int i = 1; i <= 10; i++) {
                hologram.appendTextLine(CC.translate("%practice_lb_" + ladder + "_" + i + "%"));
            }
        });
    }

    public String getNextLadder() {
        String currentLadder = ChatColor.stripColor(((TextLine) hologram.getLine(0)).getText()).split(" ")[0];
        switch (currentLadder) {
            case "NoDebuff":
                return "Debuff";
            case "Debuff":
                return "Kohi";
            case "Kohi":
                return "Sumo";
            case "Sumo":
                return "Invaded";
            case "Invaded":
                return "BuildUHC";
            case "BuildUHC":
                return "Combo";
            case "Combo":
                return "Gapple";
            case "Gapple":
                return "Archer";
            case "Archer":
                return "SoupRefill";
            case "SoupRefill":
                return "HCF";
            case "HCF":
                return "Classic";
            case "Classic":
                return "Axe";
            case "Axe":
                return "Vanilla";
            case "Vanilla":
                return "NoDebuff";
            default:
                return "";
        }
    }
}
