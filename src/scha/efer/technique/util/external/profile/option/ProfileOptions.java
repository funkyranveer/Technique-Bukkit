package scha.efer.technique.util.external.profile.option;

import lombok.Getter;
import lombok.Setter;

public class ProfileOptions {

    @Getter
    @Setter
    private boolean publicChatEnabled = true;
    @Getter
    @Setter
    private boolean privateChatEnabled = true;
    @Getter
    @Setter
    private boolean privateChatSoundsEnabled = true;

}
