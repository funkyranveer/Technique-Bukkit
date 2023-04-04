package scha.efer.technique.tablist.utils;

import lombok.*;
import org.bukkit.*;
import scha.efer.technique.tablist.ZigguratTablist;

@Getter @Setter @AllArgsConstructor
public class TabEntry {

    private String id;
    private OfflinePlayer offlinePlayer;
    private String text;
    private ZigguratTablist tab;
    private SkinTexture texture;
    private TabColumn column;
    private int slot, rawSlot, latency;

}
