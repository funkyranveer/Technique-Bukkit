package scha.efer.technique.clan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import scha.efer.technique.match.MatchSnapshot;
import scha.efer.technique.util.ConfigurationSerializableTypeAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.configuration.serialization.ConfigurationSerializable;


@RequiredArgsConstructor
public class ClanMatchHistory {
    @Getter
    private final MatchSnapshot fighter;
    @Getter
    private final MatchSnapshot opponent;

    @Getter
    private final boolean won;

    @Getter
    private final int eloChange;


    public ClanMatchHistory(Document document) {
        this.won = document.getBoolean("won");

        this.fighter = createGson().fromJson(document.getString("fighter"), MatchSnapshot.class);
        this.opponent = createGson().fromJson(document.getString("opponent"), MatchSnapshot.class);
        if (document.containsKey("eloChange")) {
            this.eloChange = document.getInteger("eloChange");
        } else {
            this.eloChange = 0;
        }
    }

    public Document toDocument() {
        Document document = new Document();
        document.put("fighter", createGson().toJson(fighter));
        document.put("opponent", createGson().toJson(opponent));
        document.put("won", won);
        document.put("eloChange", eloChange);
        return document;
    }

    private Gson createGson() {
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableTypeAdapter())
                .create();
    }
}
