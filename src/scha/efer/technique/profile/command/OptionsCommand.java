package scha.efer.technique.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.profile.options.OptionsMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = "options")
public class OptionsCommand {

    public void execute(Player player) {
        new OptionsMenu().openMenu(player);
    }

}