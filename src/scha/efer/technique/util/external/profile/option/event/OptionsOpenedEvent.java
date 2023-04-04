package scha.efer.technique.util.external.profile.option.event;

import scha.efer.technique.util.external.BaseEvent;
import scha.efer.technique.util.external.profile.option.menu.ProfileOptionButton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class OptionsOpenedEvent extends BaseEvent {

    private final Player player;
    private final List<ProfileOptionButton> buttons = new ArrayList<>();

}
