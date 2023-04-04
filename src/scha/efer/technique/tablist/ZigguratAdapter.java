package scha.efer.technique.tablist;

import org.bukkit.entity.*;
import scha.efer.technique.tablist.utils.BufferedTabObject;

import java.util.*;

public interface ZigguratAdapter {

    Set<BufferedTabObject> getSlots(Player player);

    String getFooter();

    String getHeader();

}
