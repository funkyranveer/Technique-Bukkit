package scha.efer.technique.tablist.utils.playerversion.impl;

import org.bukkit.entity.*;
import scha.efer.technique.tablist.utils.playerversion.IPlayerVersion;
import scha.efer.technique.tablist.utils.playerversion.PlayerVersion;
import us.myles.ViaVersion.api.Via;

public class PlayerVersion1_7Impl implements IPlayerVersion {

    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(
                Via.getAPI().getPlayerVersion(player)
        );
    }
}
