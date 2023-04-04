package scha.efer.technique.placeholders;

import scha.efer.technique.clan.Clan;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.kit.KitLeaderboards;
import scha.efer.technique.profile.Profile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPIExtension extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "practice";
    }

    @Override
    public String getAuthor() {
        return "Stagflasyon";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(player == null){
            return "&5None";
        }

        if (identifier.contains("global")) { //practice_global_1
            String[] splittedShit = identifier.split("_");
            int number = Integer.parseInt(splittedShit[1]) - 1;
            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = Profile.getGlobalEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&5None";
            }

            if (kitLeaderboards == null) {
                return "&5None";
            }

            return "&5" + (number + 1) + ") &f" + kitLeaderboards.getName() + " &7» &d" + kitLeaderboards.getElo();
        }

        if (identifier.contains("lb")) { //practice_lb_NoDebuff_1
            String[] splittedShit = identifier.split("_");
            String kitString = splittedShit[1];
            int number = Integer.parseInt(splittedShit[2]) - 1;
            Kit kit = Kit.getByName(kitString);

            if (kit == null) return "&cNone";

            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = kit.getRankedEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&cNone";
            }

            if (kitLeaderboards == null) {
                return "&cNone";
            }

            return "&5" + (number + 1) + ") &f" + kitLeaderboards.getName() + " &7» &d" + kitLeaderboards.getElo();
        }

        if (identifier.contains("clan")) { //practice_clan_1
            String[] splittedShit = identifier.split("_");
            int number = Integer.parseInt(splittedShit[1]) - 1;
            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = Clan.getClanEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&cNone";
            }

            if (kitLeaderboards == null) {
                return "&cNone";
            }

            return "&5" + (number + 1) + ") &f" + kitLeaderboards.getName() + " &7» &5" + kitLeaderboards.getElo();
        }

        return null;
    }
}
