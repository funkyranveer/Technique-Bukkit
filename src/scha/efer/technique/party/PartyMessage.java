package scha.efer.technique.party;

import scha.efer.technique.util.external.CC;
import lombok.AllArgsConstructor;

import java.text.MessageFormat;

@AllArgsConstructor
public enum PartyMessage {

    YOU_HAVE_BEEN_INVITED("&3[Party] &eYou have been invited to join &d{0}&e''s party."),
    CLICK_TO_JOIN("&a(Click to accept)"),
    PLAYER_INVITED("&3[Party] &d{0} &ehas been invited to your party."),
    PLAYER_JOINED("&3[Party] &d{0} &ejoined your party."),
    PLAYER_LEFT("&3[Party] &d{0} &ehas left your party."),
    CREATED("&3[Party] &aYou created a party."),
    DISBANDED("&3[Party] &cYour party has been disbanded."),
    PUBLIC("&3[Party] &d{0}&e is hosting a public party!"),
    PRIVACY_CHANGED("&3[Party] &fYour party privacy has been changed to: &e{0}");

    private final String message;

    public String format(Object... objects) {
        return CC.translate(new MessageFormat(this.message).format(objects));
    }

}
