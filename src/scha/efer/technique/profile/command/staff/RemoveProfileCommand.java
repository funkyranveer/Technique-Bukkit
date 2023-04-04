package scha.efer.technique.profile.command.staff;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import scha.efer.technique.TechniquePlugin;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "removeprofile", permission = "technique.admin")
public class RemoveProfileCommand {

    public void execute(CommandSender player, @CPL("name") String name) {
        if (name == null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "That name is not valid");
        }
        try {
            TechniquePlugin.get().getMongoDatabase().getCollection("profiles").deleteOne(new Document("name", name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.sendMessage(ChatColor.DARK_PURPLE + "Deleted: " + name);
    }

}
