package scha.efer.technique.tablist.utils.serverversion;

import org.bukkit.*;
import scha.efer.technique.tablist.utils.serverversion.impl.ServerVersionUnknownImpl;


public class ServerVersionHandler {

    public static IServerVersion version;
    public static String serverVersionName;

    public ServerVersionHandler() {
        serverVersionName = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        version = new ServerVersionUnknownImpl();
    }
}
