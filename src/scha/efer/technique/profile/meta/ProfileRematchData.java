package scha.efer.technique.profile.meta;

import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.impl.SoloMatch;
import scha.efer.technique.match.impl.SumoMatch;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.queue.QueueType;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ChatComponentBuilder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class ProfileRematchData {

    private final UUID key;
    private final UUID sender;
    private final UUID target;
    @Setter
    private Kit kit;
    @Setter
    private Arena arena;
    @Setter
    private boolean sent;
    @Setter
    private boolean receive;
    private final long timestamp = System.currentTimeMillis();

    public ProfileRematchData(UUID key, UUID sender, UUID target, Kit kit, Arena arena) {
        this.key = key;
        this.sender = sender;
        this.target = target;
        this.kit = kit;
        this.arena = arena;
    }

    public void request() {

        Player sender = TechniquePlugin.get().getServer().getPlayer(this.sender);
        Player target = TechniquePlugin.get().getServer().getPlayer(this.target);

        if (sender == null || target == null) {
            return;
        }

        Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (senderProfile.getRematchData() == null || targetProfile.getRematchData() == null ||
                !senderProfile.getRematchData().getKey().equals(targetProfile.getRematchData().getKey())) {
            return;
        }

        if (senderProfile.isBusy(sender)) {
            sender.sendMessage(CC.RED + "You cannot duel right now.");
            return;
        }

        sender.sendMessage(CC.translate("&fYou sent a rematch request to &5" + target.getName() + " &fwith kit &5" +
                kit.getName() + "&f."));
        target.sendMessage(CC.translate("&5" + sender.getName() + " &fhas sent you a rematch request with kit &5" +
                kit.getName() + "&f."));

        target.spigot().sendMessage(new ChatComponentBuilder("")
                .parse("&a(Click to accept)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse("&aClick to accept this rematch invite.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rematch"))
                .create());

        this.sent = true;
        targetProfile.getRematchData().receive = true;

        senderProfile.checkForHotbarUpdate();
        targetProfile.checkForHotbarUpdate();
        Bukkit.getScheduler().runTaskLaterAsynchronously(TechniquePlugin.get(), () -> {
            senderProfile.checkForHotbarUpdate();
            targetProfile.checkForHotbarUpdate();
        }, 15 * 20);
    }

    public void accept() {
        Player sender = TechniquePlugin.get().getServer().getPlayer(this.sender);
        Player target = TechniquePlugin.get().getServer().getPlayer(this.target);

        if (sender == null || target == null || !sender.isOnline() || !target.isOnline()) {
            return;
        }

        Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (senderProfile.getRematchData() == null || targetProfile.getRematchData() == null ||
                !senderProfile.getRematchData().getKey().equals(targetProfile.getRematchData().getKey())) {
            return;
        }

        if (senderProfile.isBusy(sender)) {
            sender.sendMessage(CC.RED + "You cannot duel right now.");
            return;
        }

        if (targetProfile.isBusy(target)) {
            sender.sendMessage(CC.translate(CC.RED + target.getDisplayName()) + CC.RED + " is currently busy.");
            return;
        }

        Arena arena = this.arena;

        if (arena.isActive()) {
            arena = Arena.getRandom(kit);
        }

        if (arena == null) {
            sender.sendMessage(CC.RED + "Tried to start a match but there are no available arenas.");
            return;
        }

        arena.setActive(true);
        Match match;

        if(kit.getGameRules().isSumo()) {
            match = new SumoMatch(null, new TeamPlayer(sender), new TeamPlayer(target), kit, arena, QueueType.UNRANKED);
        } else {
            match = new SoloMatch(null, new TeamPlayer(sender), new TeamPlayer(target), kit, arena, QueueType.UNRANKED,0,0);
        }
        match.start();
    }

}
