package scha.efer.technique.queue;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.impl.*;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QueueThread extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                for (Queue queue : Queue.getQueues()) {
                    queue.getPlayers().forEach(QueueProfile::tickRange);

                    if (queue.getPlayers().size() < 2) {
                        continue;
                    }

                    for (QueueProfile firstQueueProfile : queue.getPlayers()) {
                        final Player firstPlayer = Bukkit.getPlayer(firstQueueProfile.getPlayerUuid());

                        if (firstPlayer == null) {
                            continue;
                        }

                        final Profile firstProfile = Profile.getByUuid(firstQueueProfile.getPlayerUuid());

                        for (QueueProfile secondQueueProfile : queue.getPlayers()) {
                            if (firstQueueProfile.equals(secondQueueProfile)) {
                                continue;
                            }

                            Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getPlayerUuid());
                            Profile secondProfile = Profile.getByUuid(secondQueueProfile.getPlayerUuid());

                            if (secondPlayer == null) {
                                continue;
                            }

//							if (firstProfile.getOptions().isUsingPingFactor() ||
//							    secondProfile.getOptions().isUsingPingFactor()) {
//								if (firstPlayer.getPing() >= secondPlayer.getPing()) {
//									if (firstPlayer.getPing() - secondPlayer.getPing() >= 50) {
//										continue;
//									}
//								} else {
//									if (secondPlayer.getPing() - firstPlayer.getPing() >= 50) {
//										continue;
//									}
//								}
//							}

                            if (queue.getType() == QueueType.RANKED) {
                                if (!firstQueueProfile.isInRange(secondQueueProfile.getElo()) ||
                                        !secondQueueProfile.isInRange(firstQueueProfile.getElo())) {
                                    continue;
                                }
                            }

                            if (queue.getType() == QueueType.CLAN) {
                                Clan firstClan = Clan.getByMember(firstPlayer.getUniqueId());
                                Clan secondClan = Clan.getByMember(secondPlayer.getUniqueId());

                                if (firstClan == secondClan) {
                                    continue;
                                }
                            }
                            // Find arena
                            final Arena arena = Arena.getRandom(queue.getKit());

                            if (arena == null) {
                                continue;
                            }

                            if (arena.isActive()) continue;

                            if (queue.getKit().getGameRules().isBuild()) arena.setActive(true);

                            // Remove players from queue
                            queue.getPlayers().remove(firstQueueProfile);
                            queue.getPlayers().remove(secondQueueProfile);

                            TeamPlayer firstMatchPlayer = new TeamPlayer(firstPlayer);
                            TeamPlayer secondMatchPlayer = new TeamPlayer(secondPlayer);

                            if (queue.getType() == QueueType.RANKED) {
                                firstMatchPlayer.setElo(firstProfile.getKitData().get(queue.getKit()).getElo());
                                secondMatchPlayer.setElo(secondProfile.getKitData().get(queue.getKit()).getElo());
                            }

                            // Create match
                            Match match;
                            if(queue.getKit().getGameRules().isSumo()) {
                                match = new SumoMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType());
                            }else if (queue.getKit().getGameRules().isBuild() && queue.getKit().getGameRules().isBridge()) {
                                match = new TheBridgeMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType());
                            } else if (queue.getKit().getGameRules().isBoxing()) {
                                match = new BoxingMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType(),0,0);
                            } else if (queue.getKit().getName().equalsIgnoreCase("MLG-Rush")) {
                                match = new MLGRushMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType());
                            }
                            else if (queue.getKit().getName().equalsIgnoreCase("Stick-Fight")) {
                                match = new StickFightMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType());
                            }
                            else {
                                match = new SoloMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType(),0,0);
                            }


                            String[] opponentMessages = formatMessages(firstPlayer.getName(),
                                    secondPlayer.getName(), firstMatchPlayer.getElo(), secondMatchPlayer.getElo(),
                                    queue.getQueueType());

                            firstPlayer.sendMessage(opponentMessages[0]);
                            secondPlayer.sendMessage(opponentMessages[1]);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    match.start();
                                }
                            }.runTask(TechniquePlugin.get());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }

                continue;
            }

            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String[] formatMessages(String player1, String player2, int player1Elo, int player2Elo, QueueType type) {
        String player1Format;
        String player2Format;

        if (type == QueueType.UNRANKED) {
            player1Format = player1;
            player2Format = player2;
        } else if (type == QueueType.RANKED) {
            player1Format = player1 + CC.GRAY + " (" + player1Elo + ")";
            player2Format = player2 + CC.GRAY + " (" + player2Elo + ")";
        } else if (type == QueueType.CLAN) {
            player1Format = player1;
            player2Format = player2;
        } else {
            throw new AssertionError();
        }
        return new String[]{
                CC.GRAY + CC.BOLD + "Found opponent: " + CC.GREEN + player1Format + CC.GRAY + " vs " +
                        CC.RED + player2Format,
                CC.GRAY + CC.BOLD + "Found opponent: " + CC.GREEN + player2Format + CC.GRAY + " vs " +
                        CC.RED + player1Format
        };
    }

}
