package scha.efer.technique.tablist.utils.playerversion;

import scha.efer.technique.tablist.utils.playerversion.impl.PlayerVersion1_7Impl;

public class PlayerVersionHandler {

    public static IPlayerVersion version;

    public PlayerVersionHandler() {
        version = new PlayerVersion1_7Impl();
    }
}
