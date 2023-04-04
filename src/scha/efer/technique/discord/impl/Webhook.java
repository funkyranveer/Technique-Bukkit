package scha.efer.technique.discord.impl;


import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.discord.DiscordWebhook;
import scha.efer.technique.event.impl.tournament.Tournament;
import scha.efer.technique.util.TaskUtil;

import java.awt.*;
import java.io.IOException;

public class Webhook {

    public static void init(Tournament tournament) {
        DiscordWebhook webhook = new DiscordWebhook(TechniquePlugin.get().getMainConfig().getString("SETTINGS.DISCORD-WEBHOOK"));
        webhook.setAvatarUrl("");
        webhook.setUsername("Strafe | Tournaments");
        webhook.setTts(false);
        webhook.setContent("@TournamentAlerts");

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setImage("");
        embed.setAuthor("Tournament Alerts", null, "");
        embed.setTitle("Information");
        embed.setColor(Color.ORANGE);
        embed.addField("Kit", tournament.getLadder().getName(), true);
        embed.addField("Size", String.valueOf(tournament.getParticipants().size()), true);
        embed.addField("Limit", String.valueOf(tournament.getTeamCount()), true);
        embed.setFooter("Strafe | Tournaments", "");
        webhook.addEmbed(embed);

        TaskUtil.runAsync(() -> {
            try {
                webhook.execute();
            } catch (IOException ignore) {
                System.out.print("TOURNAMENT WEBHOOK DOESN'T SENT CORRECTLY");
            }
        });
    }

}
