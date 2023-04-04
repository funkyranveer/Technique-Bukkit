package scha.efer.technique.duel;

import scha.efer.technique.arena.Arena;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.ChatComponentBuilder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

public class DuelProcedure {

    @Getter
    private final boolean party;
    @Getter
    private final Player sender;
    @Getter
    private final Player target;
    @Getter
    @Setter
    private Kit kit;
    @Getter
    @Setter
    private Arena arena;

    public DuelProcedure(Player sender, Player target, boolean party) {
        this.sender = sender;
        this.target = target;
        this.party = party;
    }

    public void send() {
        if (!sender.isOnline() || !target.isOnline()) {
            return;
        }

        DuelRequest request = new DuelRequest(sender.getUniqueId(), party);
        request.setKit(kit);
        request.setArena(arena);

        Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        senderProfile.setDuelProcedure(null);
        senderProfile.getSentDuelRequests().put(target.getUniqueId(), request);

        sender.sendMessage(CC.translate("&3[Duels] &fYou sent a duel request to &5" + target.getName() + "&f with kit &5" + (kit.getName().equals("HCFDIAMOND") ? "HCF Event Kits" : kit.getName())));
        target.sendMessage(CC.translate("&3[Duels] &5" + sender.getName() + " &fhas sent you a duel request with kit &5" + (kit.getName().equals("HCFDIAMOND") ? "HCF Event Kits" : kit.getName())));
        target.spigot().sendMessage(new ChatComponentBuilder("")
                .parse("&a&l(Click to accept)")
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + sender.getName()))
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder(CC.GREEN + "Click to accept this duel invite.").create()))
                .create());
    }

}
