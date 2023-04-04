package scha.efer.technique.util;

import scha.efer.technique.TechniquePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
    public TaskUtil() {
    }

    public static void run(Runnable runnable) {
        TechniquePlugin.get().getServer().getScheduler().runTask(TechniquePlugin.get(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        TechniquePlugin.get().getServer().getScheduler().runTaskTimer(TechniquePlugin.get(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(TechniquePlugin.get(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        TechniquePlugin.get().getServer().getScheduler().runTaskLater(TechniquePlugin.get(), runnable, delay);
    }

    public static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(TechniquePlugin.get(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(TechniquePlugin.get(), runnable);
        else
            runnable.run();
    }
}
