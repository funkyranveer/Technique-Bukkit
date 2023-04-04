package scha.efer.technique.queue;

import org.bukkit.Sound;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileState;
import scha.efer.technique.util.external.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class Queue {

    @Getter
    private static final List<Queue> queues = new ArrayList<>();

    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter
    private final Kit kit;
    @Getter
    private final QueueType type;
    @Getter
    private final LinkedList<QueueProfile> players = new LinkedList<>();

    public Queue(Kit kit, QueueType type) {
        this.kit = kit;
        this.type = type;

        queues.add(this);
    }

    public static Queue getByUuid(UUID uuid) {
        for (Queue queue : queues) {
            if (queue.getUuid().equals(uuid)) {
                return queue;
            }
        }

        return null;
    }

    public static Queue getByPredicate(Predicate<Queue> predicate) {
        for (Queue queue : queues) {
            if (predicate.test(queue)) {
                return queue;
            }
        }

        return null;
    }

    public String getQueueName() {
        if (type == QueueType.RANKED) {
            return "Ranked " + kit.getName();
        } else if (type == QueueType.UNRANKED) {
            return "Unranked " + kit.getName();
        } else if (type == QueueType.CLAN) {
            return "Clan " + kit.getName();
        } else {
            throw new AssertionError();
        }
    }

    public Queue getRankedType() {
        if (type != QueueType.RANKED) {
            for (Queue queue : queues) {
                if (queue.getKit() == kit) {
                    if (queue.getQueueType() != type && queue.getQueueType() != QueueType.CLAN) {
                        return queue;
                    }
                }
            }
        }
        return null;
    }

    public Queue getUnrankedType() {
        if (type != QueueType.UNRANKED) {
            for (Queue queue : queues) {
                if (queue.getKit() == kit) {
                    if (queue.getQueueType() != type && queue.getQueueType() != QueueType.CLAN) {
                        return queue;
                    }
                }
            }
        }
        return null;
    }

    public Queue getClanType() {
        if (type != QueueType.CLAN) {
            for (Queue queue : queues) {
                if (queue.getKit() == kit) {
                    if (queue.getQueueType() != type && queue.getQueueType() != QueueType.UNRANKED && queue.getQueueType() != QueueType.RANKED) {
                        return queue;
                    }
                }
            }
        }
        return null;
    }

    public void addPlayer(Player player, int elo) {
        QueueProfile queueProfile = new QueueProfile(player.getUniqueId());
        queueProfile.setElo(elo);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setQueue(this);
        profile.setQueueProfile(queueProfile);
        profile.setState(ProfileState.IN_QUEUE);
        profile.refreshHotbar();

        player.sendMessage(" ");
        player.sendMessage(CC.DARK_PURPLE + CC.BOLD + getQueueName());
        player.sendMessage(CC.GRAY + CC.ITALIC + "  Searching for opponent...");
        player.sendMessage(" ");
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 5F, 5F);

        players.add(queueProfile);
    }

    public void removePlayer(QueueProfile queueProfile) {
        players.remove(queueProfile);

        Player player = Bukkit.getPlayer(queueProfile.getPlayerUuid());

        if (player != null && player.isOnline()) {
            player.sendMessage(CC.RED + "You are no longer queued for " + CC.WHITE + getQueueName() + CC.RED + ".");
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5F, 5F);
        }

        Profile profile = Profile.getByUuid(queueProfile.getPlayerUuid());
        profile.setQueue(null);
        profile.setQueueProfile(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
    }

    public QueueType getQueueType() {
        return type;
    }
}
