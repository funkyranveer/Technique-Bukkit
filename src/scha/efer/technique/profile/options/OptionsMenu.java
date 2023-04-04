package scha.efer.technique.profile.options;

import org.bukkit.entity.*;

import java.util.*;
import org.bukkit.inventory.*;
import org.bukkit.event.inventory.*;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.external.ItemBuilder;
import scha.efer.technique.util.external.menu.Button;
import scha.efer.technique.util.external.menu.Menu;

public class OptionsMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&5Options";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new OptionsButton(OptionsType.TOGGLESCOREBOARD));
        buttons.put(2, new OptionsButton(OptionsType.TOGGLEDUELREQUESTS));
        buttons.put(4, new OptionsButton(OptionsType.TOGGLESPECTATORS));
        buttons.put(6, new OptionsButton(OptionsType.TOGGLEPMS));
        buttons.put(8, new OptionsButton(OptionsType.TOGGLELIGHTNING));
        return buttons;
    }

    private class OptionsButton extends Button
    {
        private final OptionsType type;

        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            boolean lore = false;
            if (this.type == OptionsType.TOGGLESCOREBOARD) {
                lore = profile.getOptions().isShowScoreboard();
            }
            else if (this.type == OptionsType.TOGGLEDUELREQUESTS) {
                lore = profile.getOptions().isReceiveDuelRequests();
            }
            else if (this.type == OptionsType.TOGGLEPMS) {
                lore = profile.getOptions().isPrivateMessages();
            }
            else if (this.type == OptionsType.TOGGLESPECTATORS) {
                lore = profile.getOptions().isAllowSpectators();
            }
            else if (this.type == OptionsType.TOGGLELIGHTNING) {
                lore = profile.getOptions().isLightning();
            }
            return new ItemBuilder(this.type.getMaterial()).name("&5" + this.type.getName()).lore("&fEnabled: " + (lore ? "&a" : "&5") + lore).build();
        }

        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (this.type == OptionsType.TOGGLESCOREBOARD) {
                profile.getOptions().setShowScoreboard(!profile.getOptions().isShowScoreboard());
            }
            else if (this.type == OptionsType.TOGGLEDUELREQUESTS) {
                profile.getOptions().setReceiveDuelRequests(!profile.getOptions().isReceiveDuelRequests());
            }
            else if (this.type == OptionsType.TOGGLEPMS) {
                profile.getOptions().setPrivateMessages(!profile.getOptions().isPrivateMessages());
            }
            else if (this.type == OptionsType.TOGGLESPECTATORS) {
                profile.getOptions().setAllowSpectators(!profile.getOptions().isAllowSpectators());
            }
            else if (this.type == OptionsType.TOGGLELIGHTNING) {
                profile.getOptions().setLightning(!profile.getOptions().isLightning());
            }
        }

        @Override
        public boolean shouldUpdate(final Player player, final ClickType clickType) {
            return true;
        }

        public OptionsButton(final OptionsType type) {
            this.type = type;
        }
    }
}
