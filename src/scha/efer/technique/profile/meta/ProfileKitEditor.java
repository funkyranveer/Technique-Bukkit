package scha.efer.technique.profile.meta;

import scha.efer.technique.kit.Kit;
import scha.efer.technique.kit.KitLoadout;
import scha.efer.technique.profile.ProfileState;
import lombok.Getter;
import lombok.Setter;

@Setter
public class ProfileKitEditor {

    @Getter
    private boolean active;
    private boolean rename;
    @Getter
    private ProfileState previousState;
    @Getter
    private Kit selectedKit;
    @Getter
    private KitLoadout selectedKitLoadout;

    public boolean isRenaming() {
        return this.active && this.rename && this.selectedKit != null;
    }

}
