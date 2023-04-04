package scha.efer.technique.util.external;

import scha.efer.technique.TechniquePlugin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public void call() {
        TechniquePlugin.get().getServer().getPluginManager().callEvent(this);
    }

}
