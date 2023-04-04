package scha.efer.technique.arena.command;


import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.ArenaType;
import scha.efer.technique.arena.impl.TheBridgeArena;
import scha.efer.technique.arena.selection.Selection;
import scha.efer.technique.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena setportal", permission = "technique.dev")
public class ArenaSetPortalCommand {
    public void execute(Player player, @CPL("arena") String arena, @CPL("[blue|red]") String color) {
      if (!color.equals("blue") && !color.equals("red")) {
          player.sendMessage(CC.translate("&7That is an invalid team."));
          return;
      }
        Arena mainArena = Arena.getByName(arena);
      if (mainArena == null) {
          player.sendMessage(CC.translate("&7An arena with that name does not exist."));
          return;
      }

      if (mainArena.getType() != ArenaType.THEBRIDGE) {
          player.sendMessage(CC.translate("&7That arena is not a &5TheBridge &7arena."));
          return;
      }

      if (color.equalsIgnoreCase("blue")) {
          TheBridgeArena bridgeArena = (TheBridgeArena) mainArena;
          Selection selection = Selection.createOrGetSelection(player);
          if (!selection.isFullObject()) {
              player.sendMessage(CC.translate("&7Your selection is incomplete."));
              return;
          }
          bridgeArena.setBlueCuboid(selection.getCuboid());
          player.sendMessage(CC.translate("&7Successfully set the &5Blue Portal&7!"));
      }
      if (color.equalsIgnoreCase("red")) {
          TheBridgeArena bridgeArena = (TheBridgeArena) mainArena;
          Selection selection = Selection.createOrGetSelection(player);
          if (!selection.isFullObject()) {
              player.sendMessage(CC.translate("&7Your selection is incomplete."));
              return;
          }
          bridgeArena.setRedCuboid(selection.getCuboid());
          player.sendMessage(CC.translate("&7Successfully set the &5Red Portal&7!"));
      }
    }
}
