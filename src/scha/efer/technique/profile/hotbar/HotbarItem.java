package scha.efer.technique.profile.hotbar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HotbarItem {

    QUEUE_JOIN_RANKED(null),
    QUEUE_JOIN_UNRANKED(null),
    QUEUE_JOIN_CLAN(null),
    PLAY_GAME(null),
    JOIN_FFA(null),
    PACK_SHOWCASE(null),
    LEAVE_SHOWCASE(null),

    QUEUE_LEAVE(null),
    PARTY_EVENTS(null),
    PARTY_CREATE("party create"),
    PARTY_DISBAND("party disband"),
    PARTY_LEAVE("party leave"),
    LEADERBOARDS(null),
    PARTY_INFORMATION("party info"),
    PARTY_SETTINGS(null),
    OTHER_PARTIES(null),
    PARTY_MEMBERS(null),
    STATS_MENU(null),
    LEADERBOARDS_MENU(null),
    KIT_EDITOR(null),
    SPECTATE_STOP("stopspectating"),
    VIEW_INVENTORY(null),
    EVENT_JOIN("event"),
    SUMO_LEAVE("sumo leave"),
    BRACKETS_LEAVE("brackets leave"),
    FFA_LEAVE("ffa leave"),
    JUGGERNAUT_LEAVE("juggernaut leave"),
    PARKOUR_LEAVE("parkour leave"),
    WIPEOUT_LEAVE("wipeout leave"),
    SKYWARS_LEAVE("skywars leave"),
    SPLEEF_LEAVE("spleef leave"),
    INFECTED_LEAVE("infected leave"),
    REMATCH_REQUEST("rematch"),
    DEFAULT_KIT(null),
    DIAMOND_KIT(null),
    BARD_KIT(null),
    ROGUE_KIT(null),
    ARCHER_KIT(null),
    MATCH_END(null);
    private final String command;

}
