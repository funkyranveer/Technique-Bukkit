package scha.efer.technique.clan;

import scha.efer.technique.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;

public class ClanPlayer {
    private final UUID uuid;
    @Getter
    @Setter
    private ClanRole role;
    private String lastKnownName;

    public ClanPlayer(UUID uuid) {
        this.uuid = uuid;
        this.role = ClanRole.MEMBER;
        this.lastKnownName = Bukkit.getPlayer(uuid).getName();
    }

    public ClanPlayer(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.role = ClanRole.valueOf(document.getString("role"));

        String name = Bukkit.getOfflinePlayer(uuid).getName();

        if (name != null) {
            this.lastKnownName = name;
        } else {
            this.lastKnownName = Profile.getByUuid(uuid).getName();
        }

    }

    public Document toDocument() {
        Document document = new Document();
        document.put("uuid", this.uuid.toString());
        document.put("role", this.role.name());
        return document;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return lastKnownName;
    }

    public void setName(String name) {
        this.lastKnownName = name;
    }

}
