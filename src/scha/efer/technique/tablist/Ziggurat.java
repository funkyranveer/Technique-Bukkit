package scha.efer.technique.tablist;

import lombok.*;
import org.bukkit.event.*;
import org.bukkit.plugin.java.*;
import scha.efer.technique.tablist.utils.IZigguratHelper;
import scha.efer.technique.tablist.utils.impl.v1_7TabImpl;
import scha.efer.technique.tablist.utils.playerversion.PlayerVersionHandler;
import scha.efer.technique.tablist.utils.serverversion.ServerVersionHandler;

import java.util.*;
import java.util.concurrent.*;

@Getter
public class Ziggurat {

    //Instance
    @Getter
    private static Ziggurat instance;

    private JavaPlugin plugin;
    private ZigguratAdapter adapter;
    private Map<UUID, ZigguratTablist> tablists;
    private ZigguratThread thread;
    private IZigguratHelper implementation;
    private ZigguratListeners listeners;

    //Tablist Ticks
    @Setter
    private long ticks = 1;
    @Setter
    private boolean hook = false;

    public Ziggurat(JavaPlugin plugin, ZigguratAdapter adapter) {
        if (instance != null) {
            throw new RuntimeException("Ziggurat has already been instatiated!");
        }

        if (plugin == null) {
            throw new RuntimeException("Ziggurat can not be instantiated without a plugin instance!");
        }

        instance = this;

        this.plugin = plugin;
        this.adapter = adapter;
        this.tablists = new ConcurrentHashMap<>();

        new ServerVersionHandler();
        new PlayerVersionHandler();

        this.registerImplementation();

        this.setup();
    }

    private void registerImplementation() {
        this.implementation = new v1_7TabImpl();
    }

    private void setup() {
        listeners = new ZigguratListeners();
        //Register Events
        this.plugin.getServer().getPluginManager().registerEvents(listeners, this.plugin);

        //Ensure that the thread has stopped running
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        //Start SThread
        this.thread = new ZigguratThread(this);
    }

    public void disable() {
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        if (listeners != null) {
            HandlerList.unregisterAll(listeners);
            listeners = null;
        }
    }
}
