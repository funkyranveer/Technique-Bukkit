package scha.efer.technique.profile.options;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum OptionsType {

    TOGGLESCOREBOARD("Toggle Scoreboard", Material.ITEM_FRAME),
    TOGGLEDUELREQUESTS("Toggle Duel Requests", Material.DIAMOND_SWORD),
    TOGGLESPECTATORS("Toggle Spectators", Material.ENDER_PEARL),
    TOGGLEPMS("Toggle Private Messages", Material.BOOK_AND_QUILL),
    TOGGLELIGHTNING("Toggle Lightning Death Animation", Material.BLAZE_ROD);

    private final String name;
    private final Material material;
}
