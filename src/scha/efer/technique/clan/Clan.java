package scha.efer.technique.clan;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.kit.KitLeaderboards;
import scha.efer.technique.match.MatchSnapshot;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.util.Color;
import scha.efer.technique.util.external.CC;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Clan {
    private static final Map<String, Clan> clans = new HashMap<>();
    private static final Map<UUID, Clan> byUUIDMapping = new HashMap<>();
    @Getter
    private static final List<KitLeaderboards> clanEloLeaderboards = new ArrayList<>();

    private static MongoCollection<Document> collection;


    private UUID uniqueId;
    @Getter
    private String name;
    private Map<UUID, ClanPlayer> members;
    @Getter
    private List<UUID> invitations;
    @Getter
    private int elo = 1000;
    @Getter
    private int wins;
    @Getter
    private int loses;

    @Getter
    private String description;

    @Getter
    private Date createdAt;

    @Getter
    private List<ClanMatchHistory> matchhistory;

    private Clan() {
    }

    private Clan(String name) {
        this.uniqueId = UUID.randomUUID();
        this.name = name;
        this.members = Maps.newHashMap();
        this.invitations = Lists.newArrayList();
        this.createdAt = new Date();
        this.matchhistory = Lists.newArrayList();
    }

    public static void create(Player player, String name) {
        Preconditions.checkState(!byUUIDMapping.containsKey(player.getUniqueId()), "Already in a clan!");
        Preconditions.checkState(!clans.containsKey(name), "Already have a clan of that name!");
        Clan clan = new Clan(name);
        ClanPlayer clanPlayer = new ClanPlayer(player.getUniqueId());
        clanPlayer.setRole(ClanRole.LEADER);
        clan.members.put(clanPlayer.getUniqueId(), clanPlayer);
        clans.put(clan.getName(), clan);
        for (ClanPlayer member : clan.getMembers()) {
            byUUIDMapping.put(member.getUniqueId(), clan);
        }
        clan.saveAsync();
        clan.apply(player);
    }

    public static void init() {
        collection = TechniquePlugin.get().getMongoDatabase().getCollection("clans");
        collection.find().forEach((Consumer<Document>) document -> {
            Clan clan = new Clan();
            clan.load0(document);
            clans.put(clan.getName(), clan);
            for (ClanPlayer member : clan.getMembers()) {
                Bukkit.getOfflinePlayer(member.getUniqueId()).getName();
                byUUIDMapping.put(member.getUniqueId(), clan);
            }
        });
        // Load clan elo leaderboards
        new BukkitRunnable() {
            @Override
            public void run() {
                loadClanLeaderboards();
            }
        }.runTaskTimerAsynchronously(TechniquePlugin.get(), 20L, 600L);
    }

    public static Clan getClan(String name) {
        return clans.get(name);
    }

    public static Clan getByMember(UUID uuid) {
        return byUUIDMapping.get(uuid);
    }

    public static void reset() {
        clans.clear();
        byUUIDMapping.clear();
        collection.drop();
    }

    public static Collection<Clan> getAllClans() {
        return clans.values();
    }

    public static boolean checkValidityAndSend(Player player, String name) {
        AtomicBoolean inUse = new AtomicBoolean(false);
        getAllClans().forEach(clan -> {
            if(clan.getName().equalsIgnoreCase(name)) {
                inUse.set(true);
            }
        });
        if(inUse.get()) {
            player.sendMessage(Color.translate("&5Clan '" + name + "' already exists"));
            return false;
        }
        if (name.length() < 3) {
            player.sendMessage(Color.translate("&5Clan names must have at least 3 characters"));
            return false;
        }
        if (name.length() > 8) {
            player.sendMessage(Color.translate("&5Clan names must be less than 8 characters"));
            return false;
        }
        if (!StringUtils.isAlphanumeric(name)) {
            player.sendMessage(Color.translate("&5Clan name must be alphanumeric"));
            return false;
        }
        return true;
    }

    public static void loadClanLeaderboards() {
        if (!getClanEloLeaderboards().isEmpty()) getClanEloLeaderboards().clear();
        for (Document document : Clan.collection.find().sort(Sorts.descending("elo")).limit(10).into(new ArrayList<>())) {
            KitLeaderboards kitLeaderboards = new KitLeaderboards();
            kitLeaderboards.setName((String) document.get("name"));
            kitLeaderboards.setElo((Integer) document.get("elo"));
            getClanEloLeaderboards().add(kitLeaderboards);
        }
    }

    private void load0(Document document) {
        if (document.containsKey("uniqueId")) {
            this.uniqueId = UUID.fromString(document.getString("uniqueId"));
        } else {
            this.uniqueId = UUID.randomUUID();
        }
        this.name = document.getString("name");
        List<Document> documents = (List<Document>) document.get("members");
        this.members = Maps.newHashMap();
        for (Document children : documents) {
            ClanPlayer clanPlayer = new ClanPlayer(children);
            members.put(clanPlayer.getUniqueId(), clanPlayer);
        }
        this.invitations = (List<UUID>) document.get("invitations");
        this.elo = document.getInteger("elo", 1000);
        this.description = document.getString("description");
        if (document.containsKey("createdAt")) {
            this.createdAt = document.getDate("createdAt");
        } else {
            this.createdAt = new Date();
        }

        this.matchhistory = Lists.newArrayList();

        if (document.containsKey("matchhistory")) {
            List<Document> matchHistoryDocument = (List<Document>) document.get("matchhistory");
            for (Document children : matchHistoryDocument) {
                try {
                    this.matchhistory.add(new ClanMatchHistory(children));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.loses = document.getInteger("loses", 0);
        this.wins = document.getInteger("wins", 0);
    }

    public void saveAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskAsynchronously(TechniquePlugin.get());
    }

    public void save() {
        Document document = new Document();
        document.put("uniqueId", uniqueId.toString());
        document.put("name", name);
        List<Document> memberdocument = Lists.newArrayList();
        for (ClanPlayer clanPlayer : members.values()) {
            memberdocument.add(clanPlayer.toDocument());
        }
        document.put("members", memberdocument);
        document.put("invitations", invitations);
        document.put("elo", this.elo);
        document.put("description", this.description);
        document.put("createdAt", createdAt);

        List<Document> matchHistorydocument = Lists.newArrayList();
        for (ClanMatchHistory history : matchhistory) {
            matchHistorydocument.add(history.toDocument());
        }
        document.put("matchhistory", matchHistorydocument);
        document.put("wins", wins);
        document.put("loses", loses);

        collection.replaceOne(Filters.eq("name", name), document, new ReplaceOptions().upsert(true));
        System.out.println("Successfuly save clan " + getName());
    }

    public void delete() {
        collection.deleteOne(Filters.eq("name", name));
        for (ClanPlayer member : members.values()) {
            Clan.byUUIDMapping.remove(member.getUniqueId());
            Player online = Bukkit.getPlayer(member.getUniqueId());
            if(online != null){
                reset(online);
            }
        }
        Clan.clans.remove(this.name);
        this.name = null;
        this.members.clear();
    }

    public void join(Player player) {
        members.put(player.getUniqueId(), new ClanPlayer(player.getUniqueId()));
        Clan.byUUIDMapping.put(player.getUniqueId(), this);
        apply(player);
        saveAsync();
    }

    public static void reset(Player player) {
    }

    public void leave(UUID uuid) {
        Clan.byUUIDMapping.remove(uuid);
        members.remove(uuid);
        saveAsync();
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
            Profile.getByUuid(online.getUniqueId()).handleVisibility();
        }
        for (Player other : getPlayerWhereOnline()) {
            Profile.getByUuid(other.getUniqueId()).handleVisibility();
        }
    }

    public ClanPlayer getClanPlayer(UUID uuid) {
        return members.get(uuid);
    }

    public Collection<Player> getPlayerWhereOnline() {
        List<Player> players = Lists.newArrayList();
        for (ClanPlayer clanPlayer : getMembers()) {
            Player bukkit = Bukkit.getPlayer(clanPlayer.getUniqueId());
            if (bukkit != null) {
                players.add(bukkit);
            }
        }
        return players;
    }

    public Collection<ClanPlayer> getMembers() {
        return members.values();
    }

    public void apply(Player player) {
        Profile.getByUuid(player.getUniqueId()).handleVisibility(); //no shane doesnt want it
        for (Player other : getPlayerWhereOnline()) {
            Profile.getByUuid(other.getUniqueId()).handleVisibility();
        }
    }

    public void addMatchHistory(ClanMatchHistory clanMatchHistory) {
        while (matchhistory.size() > 5) {
            matchhistory.remove(0);
        }
        matchhistory.add(clanMatchHistory);
        saveAsync();
    }

    public MatchSnapshot getMatchSnapShot(UUID uuid) {
        for (ClanMatchHistory clanMatchHistory : matchhistory) {
            if (clanMatchHistory.getFighter().getUniqueID().equals(uuid)) {
                return clanMatchHistory.getFighter();
            }
            if (clanMatchHistory.getOpponent().getUniqueID().equals(uuid)) {
                return clanMatchHistory.getOpponent();
            }
        }
        return null;
    }

    public void broadcast(String message) {
        for (Player player : getPlayerWhereOnline()) {
            player.sendMessage(CC.translate(message));
        }
    }

    public void setElo(int elo) {
        this.elo = elo;
        saveAsync();
    }

    public void setWins(int wins) {
        this.wins = wins;
        saveAsync();
    }

    public void setLoses(int loses) {
        this.loses = loses;
        saveAsync();
    }

    public void setDescription(String description) {
        this.description = description;
        saveAsync();
    }

    public void setName(String name) {
        String oldname = this.name;
        this.name = name;
        clans.remove(oldname);
        clans.put(name, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                collection.deleteOne(Filters.eq("name", oldname));
                save();
            }
        }.runTaskAsynchronously(TechniquePlugin.get());
    }
}
