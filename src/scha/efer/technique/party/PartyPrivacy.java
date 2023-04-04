package scha.efer.technique.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PartyPrivacy {

    OPEN("Open"),
    CLOSED("Closed");

    private final String readable;

}
