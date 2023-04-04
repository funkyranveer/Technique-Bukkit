package scha.efer.technique.profile;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import gg.smok.core.utilities.disguise.DisguiseHook;

import org.bukkit.scheduler.BukkitTask;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.duel.DuelProcedure;
import scha.efer.technique.duel.DuelRequest;
import scha.efer.technique.event.impl.brackets.Brackets;
import scha.efer.technique.event.impl.brackets.player.BracketsPlayer;
import scha.efer.technique.event.impl.brackets.player.BracketsPlayerState;
import scha.efer.technique.event.impl.infected.Infected;
import scha.efer.technique.event.impl.infected.player.InfectedPlayer;
import scha.efer.technique.event.impl.infected.player.InfectedPlayerState;
import scha.efer.technique.event.impl.juggernaut.Juggernaut;
import scha.efer.technique.event.impl.juggernaut.player.JuggernautPlayer;
import scha.efer.technique.event.impl.juggernaut.player.JuggernautPlayerState;
import scha.efer.technique.event.impl.lms.LMS;
import scha.efer.technique.event.impl.lms.player.LMSPlayer;
import scha.efer.technique.event.impl.lms.player.LMSPlayerState;
import scha.efer.technique.event.impl.parkour.Parkour;
import scha.efer.technique.event.impl.parkour.player.ParkourPlayer;
import scha.efer.technique.event.impl.parkour.player.ParkourPlayerState;
import scha.efer.technique.event.impl.skywars.SkyWars;
import scha.efer.technique.event.impl.skywars.player.SkyWarsPlayer;
import scha.efer.technique.event.impl.skywars.player.SkyWarsPlayerState;
import scha.efer.technique.event.impl.spleef.Spleef;
import scha.efer.technique.event.impl.spleef.player.SpleefPlayer;
import scha.efer.technique.event.impl.spleef.player.SpleefPlayerState;
import scha.efer.technique.event.impl.sumo.Sumo;
import scha.efer.technique.event.impl.sumo.player.SumoPlayer;
import scha.efer.technique.event.impl.sumo.player.SumoPlayerState;
import scha.efer.technique.event.impl.tournament.Tournament;
import scha.efer.technique.event.impl.wipeout.Wipeout;
import scha.efer.technique.event.impl.wipeout.player.WipeoutPlayer;
import scha.efer.technique.event.impl.wipeout.player.WipeoutPlayerState;
import scha.efer.technique.ffa.FFA;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.kit.KitLeaderboards;
import scha.efer.technique.kit.KitLoadout;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.team.TeamPlayer;
import scha.efer.technique.party.Party;
import scha.efer.technique.profile.hotbar.Hotbar;
import scha.efer.technique.profile.hotbar.HotbarItem;
import scha.efer.technique.profile.hotbar.HotbarLayout;
import scha.efer.technique.profile.meta.ProfileKitData;
import scha.efer.technique.profile.meta.ProfileKitEditor;
import scha.efer.technique.profile.meta.ProfileMatchHistory;
import scha.efer.technique.profile.meta.ProfileRematchData;
import scha.efer.technique.profile.meta.essentials.PackStatus;
import scha.efer.technique.profile.meta.essentials.ProfileEssentials;
import scha.efer.technique.profile.meta.option.ProfileOptions;
import scha.efer.technique.queue.Queue;
import scha.efer.technique.queue.QueueProfile;
import scha.efer.technique.util.InventoryUtil;
import scha.efer.technique.util.PlayerUtil;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.Cooldown;
import lombok.Getter;
import lombok.Setter;
import net.haoshoku.nick.NickPlugin;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Profile {
    @Getter
    private static final Map<UUID, Profile> profiles = new HashMap<>();
    @Getter
    private static final List<KitLeaderboards> globalEloLeaderboards = new ArrayList<>();
    @Getter
    private static Map<Integer, String> eloLeagues = new HashMap<>();
    @Getter
    private static MongoCollection<Document> allProfiles;
    private static MongoCollection<Document> collection;
    @Getter
    private final ProfileEssentials essentials = new ProfileEssentials();
    @Getter @Setter int bridgeRounds = 0;
    @Getter
    private final ProfileOptions options = new ProfileOptions();
    @Getter
    private final ProfileKitEditor kitEditor = new ProfileKitEditor();
    @Getter
    private final Map<Kit, ProfileKitData> kitData = new LinkedHashMap<>();
    @Getter
    @Setter
    private PackStatus packStatus = PackStatus.DIAMOND;
    @Getter
    private HashMap<Player, BukkitTask> playerArrowTask;
    @Getter
    @Setter
    String name;
    @Getter
    @Setter
    int globalElo = 1000;
    @Getter
    @Setter
    int brokenBeds = 0;
    @Getter
    @Setter
    int sumoRounds = 0;
    @Getter
    private final UUID uuid;
    @Getter
    @Setter
    private ProfileState state;
    @Getter
    @Setter
    private Party party;
    @Getter
    @Setter
    private Match match;
    @Getter
    @Setter
    private Sumo sumo;
    @Getter
    @Setter
    private Brackets brackets;
    @Getter
    @Setter
    private LMS lms;
    @Getter
    @Setter
    private Juggernaut juggernaut;
    @Getter
    @Setter
    private Parkour parkour;
    @Getter
    @Setter
    private Wipeout wipeout;
    @Getter
    @Setter
    private SkyWars skyWars;
    @Getter
    @Setter
    private Spleef spleef;
    @Getter
    @Setter
    private Infected infected;
    @Getter
    @Setter
    private Queue queue;
    @Getter
    @Setter
    private QueueProfile queueProfile;
    @Getter
    @Setter
    private Cooldown enderpearlCooldown = new Cooldown(0);
    @Getter
    private final Map<UUID, DuelRequest> sentDuelRequests = new HashMap<>();
    @Getter
    @Setter
    private DuelProcedure duelProcedure;
    @Getter
    @Setter
    private ProfileRematchData rematchData;
    @Getter
    @Setter
    private Player lastMessager;
    @Getter
    @Setter
    private boolean socialSpy = false;
    @Getter
    @Setter
    private boolean silent = false;
    @Getter
    @Setter
    private boolean followMode = false;
    @Getter
    @Setter
    private boolean visibility = false;
    @Getter
    @Setter
    private Player following;
    @Getter
    @Setter
    private long lastRunVisibility = 0L;
    @Getter
    @Setter
    private List<Player> follower = new ArrayList<>();
    @Getter
    @Setter
    private Player spectating;
    @Getter
    private List<ProfileMatchHistory> matchHistory = new ArrayList<>();

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.state = ProfileState.IN_LOBBY;

        for (Kit kit : Kit.getKits()) {
            this.kitData.put(kit, new ProfileKitData());
        }
        this.calculateGlobalElo();
    }

    public static void init() {
        collection = TechniquePlugin.get().getMongoDatabase().getCollection("profiles");

        // Players might have joined before the plugin finished loading
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = new Profile(player.getUniqueId());

            try {
                profile.load();
            } catch (Exception e) {
                player.kickPlayer(CC.RED + "The server is loading...");
                continue;
            }

            profiles.put(player.getUniqueId(), profile);
        }

        getEloLeagues().put(1019, "&5Diamond 3");
        getEloLeagues().put(1018, "&5Diamond 3");
        getEloLeagues().put(1017, "&5Diamond 2");
        getEloLeagues().put(1016, "&5Diamond 2");
        getEloLeagues().put(1015, "&5Diamond 1");
        getEloLeagues().put(1014, "&5Diamond 1");
        getEloLeagues().put(1013, "&6Gold 3");
        getEloLeagues().put(1012, "&6Gold 3");
        getEloLeagues().put(1011, "&6Gold 2");
        getEloLeagues().put(1010, "&6Gold 2");
        getEloLeagues().put(1009, "&6Gold 1");
        getEloLeagues().put(1008, "&6Gold 1");
        getEloLeagues().put(1007, "&7Silver 4");
        getEloLeagues().put(1006, "&7Silver 4");
        getEloLeagues().put(1005, "&7Silver 3");
        getEloLeagues().put(1004, "&7Silver 3");
        getEloLeagues().put(1003, "&7Silver 2");
        getEloLeagues().put(1002, "&7Silver 2");
        getEloLeagues().put(1001, "&7Silver 1");
        getEloLeagues().put(1000, "&7Silver 1");
        getEloLeagues().put(999, "&8Bronze 5");
        getEloLeagues().put(998, "&8Bronze 5");
        getEloLeagues().put(997, "&8Bronze 4");
        getEloLeagues().put(996, "&8Bronze 4");
        getEloLeagues().put(995, "&8Bronze 3");
        getEloLeagues().put(994, "&8Bronze 3");
        getEloLeagues().put(993, "&8Bronze 2");
        getEloLeagues().put(992, "&8Bronze 2");
        getEloLeagues().put(991, "&8Bronze 1");
        getEloLeagues().put(990, "&8Bronze 1");
        eloLeagues = eloLeagues.entrySet().stream()
                .sorted(Map.Entry.<Integer, String>comparingByKey().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        // Save every minute to prevent data loss
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : Profile.getProfiles().values()) {
                    profile.save();
                }
            }
        }.runTaskTimerAsynchronously(TechniquePlugin.get(), 36000L, 36000L);

        // Load all players from database
        Profile.loadAllProfiles();
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile.loadAllProfiles();
                Kit.getKits().forEach(Kit::updateKitLeaderboards);
            }
        }.runTaskTimerAsynchronously(TechniquePlugin.get(), 600L, 600L);

        // Reload global elo leaderboards
        new BukkitRunnable() {
            @Override
            public void run() {
                loadGlobalLeaderboards();
            }
        }.runTaskTimerAsynchronously(TechniquePlugin.get(), 600L, 600L);

        // Refresh players' hotbars every 3 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : Profile.getProfiles().values()) {
                    profile.checkForHotbarUpdate();
                }
            }
        }.runTaskTimerAsynchronously(TechniquePlugin.get(), 60L, 60L);
    }

    public static Profile getByUuid(UUID uuid) {
        Profile profile = profiles.get(uuid);

        if (profile == null) {
            profile = new Profile(uuid);
        }

        return profile;
    }

    public static Profile getByUuid(Player player) {
        Profile profile = profiles.get(player.getUniqueId());

        if (profile == null) {
            profile = new Profile(player.getUniqueId());
        }

        return profile;
    }

    public static void loadAllProfiles() {
        allProfiles = TechniquePlugin.get().getMongoDatabase().getCollection("profiles");
    }

    public static void loadGlobalLeaderboards() {
        if (!getGlobalEloLeaderboards().isEmpty()) getGlobalEloLeaderboards().clear();
        for (Document document : Profile.getAllProfiles().find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>())) {
            KitLeaderboards kitLeaderboards = new KitLeaderboards();
            kitLeaderboards.setName((String) document.get("name"));
            kitLeaderboards.setElo((Integer) document.get("globalElo"));
            getGlobalEloLeaderboards().add(kitLeaderboards);
        }
    }

    public void load() {
        Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            this.save();
            return;
        }

        this.globalElo = document.getInteger("globalElo");

        Document essentials = (Document) document.get("essentials");

        if (essentials == null) {
            Document essentialsDocument = new Document();
            essentialsDocument.put("nick", null);
            document.put("essentials", essentialsDocument);
        } else {
            this.essentials.setNick(essentials.getString("nick"));
        }

        Document options = (Document) document.get("options");

        this.options.setShowScoreboard(options.getBoolean("showScoreboard"));
        this.options.setAllowSpectators(options.getBoolean("allowSpectators"));
        this.options.setReceiveDuelRequests(options.getBoolean("receiveDuelRequests"));

        Document kitStatistics = (Document) document.get("kitStatistics");

        for (String key : kitStatistics.keySet()) {
            Document kitDocument = (Document) kitStatistics.get(key);
            Kit kit = Kit.getByName(key);

            if (kit != null) {
                ProfileKitData profileKitData = new ProfileKitData();
                profileKitData.setElo(kitDocument.getInteger("elo"));
                profileKitData.setRankedWon(kitDocument.getInteger("rankedWon"));
                profileKitData.setRankedLost(kitDocument.getInteger("rankedLost"));
                profileKitData.setUnrankedWon(kitDocument.getInteger("unrankedWon"));
                profileKitData.setUnrankedLost(kitDocument.getInteger("unrankedLost"));

                kitData.put(kit, profileKitData);
            }
        }

        Document kitsDocument = (Document) document.get("loadouts");

        for (String key : kitsDocument.keySet()) {
            Kit kit = Kit.getByName(key);

            if (kit != null) {
                JsonArray kitsArray = new JsonParser().parse(kitsDocument.getString(key)).getAsJsonArray();
                KitLoadout[] loadouts = new KitLoadout[4];

                for (JsonElement kitElement : kitsArray) {
                    JsonObject kitObject = kitElement.getAsJsonObject();

                    KitLoadout loadout = new KitLoadout(kitObject.get("name").getAsString());
                    loadout.setArmor(InventoryUtil.deserializeInventory(kitObject.get("armor").getAsString()));
                    loadout.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

                    loadouts[kitObject.get("index").getAsInt()] = loadout;
                }

                kitData.get(kit).setLoadouts(loadouts);
            }
        }

        this.matchHistory = Lists.newArrayList();

        if (document.containsKey("matchHistory")) {
            List<Document> matchHistoryDocument = (List<Document>) document.get("matchHistory");
            for (Document children : matchHistoryDocument) {
                try {
                    this.matchHistory.add(new ProfileMatchHistory(children));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", Bukkit.getOfflinePlayer(uuid).getName());
        document.put("globalElo", globalElo);

        Document essentialsDocument = new Document();
        essentialsDocument.put("nick", essentials.getNick());
        document.put("essentials", essentialsDocument);

        Document optionsDocument = new Document();
        optionsDocument.put("showScoreboard", options.isShowScoreboard());
        optionsDocument.put("allowSpectators", options.isAllowSpectators());
        optionsDocument.put("receiveDuelRequests", options.isReceiveDuelRequests());
        document.put("options", optionsDocument);

        Document kitStatisticsDocument = new Document();

        for (Map.Entry<Kit, ProfileKitData> entry : kitData.entrySet()) {
            Document kitDocument = new Document();
            kitDocument.put("elo", entry.getValue().getElo());
            kitDocument.put("rankedWon", entry.getValue().getRankedWon());
            kitDocument.put("rankedLost", entry.getValue().getRankedLost());
            kitDocument.put("unrankedWon", entry.getValue().getUnrankedWon());
            kitDocument.put("unrankedLost", entry.getValue().getUnrankedLost());
            kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
        }
        document.put("kitStatistics", kitStatisticsDocument);

        List<Document> matchHistoryDocument = Lists.newArrayList();
        for (ProfileMatchHistory history : matchHistory) {
            matchHistoryDocument.add(history.toDocument());
        }
        document.put("matchHistory", matchHistoryDocument);

        Document kitsDocument = new Document();

        for (Map.Entry<Kit, ProfileKitData> entry : kitData.entrySet()) {
            JsonArray kitsArray = new JsonArray();

            for (int i = 0; i < 4; i++) {
                KitLoadout loadout = entry.getValue().getLoadout(i);

                if (loadout != null) {
                    JsonObject kitObject = new JsonObject();
                    kitObject.addProperty("index", i);
                    kitObject.addProperty("name", loadout.getCustomName());
                    kitObject.addProperty("armor", InventoryUtil.serializeInventory(loadout.getArmor()));
                    kitObject.addProperty("contents", InventoryUtil.serializeInventory(loadout.getContents()));
                    kitsArray.add(kitObject);
                }
            }

            kitsDocument.put(entry.getKey().getName(), kitsArray.toString());
        }

        document.put("loadouts", kitsDocument);

        collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

    public void calculateGlobalElo() {
        int globalElo = 0;
        int kitCounter = 0;
        for (Kit kit : this.kitData.keySet()) {
            if (kit.getGameRules().isRanked()) {
                globalElo += this.kitData.get(kit).getElo();
                kitCounter++;
            }
        }
        this.globalElo = Math.round(globalElo / kitCounter);
    }

    public String getEloLeague() {
        String toReturn = "&8Bronze 1";
        for (Integer elo : getEloLeagues().keySet()) {
            if (this.globalElo >= elo) {
                toReturn = getEloLeagues().get(elo);
                break;
            }
        }
        if (this.globalElo >= 1020) toReturn = "&5&lChampion";
        return toReturn;
    }

    public Integer getTotalUnrankedWins() {
        return this.kitData.values().stream().mapToInt(ProfileKitData::getUnrankedWon).sum();
    }

    public Integer getTotalRankedWins() {
        return this.kitData.values().stream().mapToInt(ProfileKitData::getRankedWon).sum();
    }

    public Integer getTotalUnrankedLosses() {
        return this.kitData.values().stream().mapToInt(ProfileKitData::getUnrankedLost).sum();
    }

    public Integer getTotalRankedLosses() {
        return this.kitData.values().stream().mapToInt(ProfileKitData::getRankedLost).sum();
    }

    public void addMatchHistory(ProfileMatchHistory profileMatchHistory) {
        while (matchHistory.size() > 54) {
            matchHistory.remove(0);
        }
        while (matchHistory.size() > 53) {
            matchHistory.remove(0);
        }
        matchHistory.add(profileMatchHistory);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean canSendDuelRequest(Player player) {
        if (!sentDuelRequests.containsKey(player.getUniqueId())) {
            return true;
        }

        DuelRequest request = sentDuelRequests.get(player.getUniqueId());

        if (request.isExpired()) {
            sentDuelRequests.remove(player.getUniqueId());
            return true;
        } else {
            return false;
        }
    }

    public boolean isPendingDuelRequest(Player player) {
        if (!sentDuelRequests.containsKey(player.getUniqueId())) {
            return false;
        }

        DuelRequest request = sentDuelRequests.get(player.getUniqueId());

        if (request.isExpired()) {
            sentDuelRequests.remove(player.getUniqueId());
            return false;
        } else {
            return true;
        }
    }

    public boolean isInLobby() {
        return state == ProfileState.IN_LOBBY;
    }

    public boolean isInFFA() {
        return state == ProfileState.IN_FFA;
    }

    public boolean isAtSpawn() {
        return state == ProfileState.IN_LOBBY || state == ProfileState.IN_QUEUE;
    }

    public boolean isInQueue() {
        return state == ProfileState.IN_QUEUE && queue != null && queueProfile != null;
    }

    public boolean isInMatch() {
        return match != null;
    }

    public boolean isInFight() {
        return state == ProfileState.IN_FIGHT && match != null;
    }

    public boolean isSpectating() {
        return state == ProfileState.SPECTATE_MATCH && (
                match != null ||
                        sumo != null ||
                        brackets != null ||
                        lms != null ||
                        juggernaut != null ||
                        parkour != null ||
                        wipeout != null ||
                        skyWars != null ||
                        spleef != null ||
                        infected != null);
    }

    public boolean isInEvent() {
        return state == ProfileState.IN_EVENT;
    }

    public boolean isInTournament(Player player) {
        if (Tournament.CURRENT_TOURNAMENT != null) {
            return Tournament.CURRENT_TOURNAMENT.isParticipating(player);
        } else {
            return false;
        }
    }

    public boolean isInSumo() {
        return state == ProfileState.IN_EVENT && sumo != null;
    }

    public boolean isInBrackets() {
        return state == ProfileState.IN_EVENT && brackets != null;
    }

    public boolean isInLMS() {
        return state == ProfileState.IN_EVENT && lms != null;
    }

    public boolean isInJuggernaut() {
        return state == ProfileState.IN_EVENT && juggernaut != null;
    }

    public boolean isInParkour() {
        return state == ProfileState.IN_EVENT && parkour != null;
    }

    public boolean isInWipeout() {
        return state == ProfileState.IN_EVENT && wipeout != null;
    }

    public boolean isInSkyWars() {
        return state == ProfileState.IN_EVENT && skyWars != null;
    }

    public boolean isInSpleef() {
        return state == ProfileState.IN_EVENT && spleef != null;
    }

    public boolean isInInfected() {
        return state == ProfileState.IN_EVENT && infected != null;
    }

    public boolean isInSomeSortOfFight() {
        return (state == ProfileState.IN_FIGHT && match != null) || (state == ProfileState.IN_EVENT);

    }

    public boolean isBusy(Player player) {
        return isInQueue() || isInFFA() || isInFight() || isInEvent() || isSpectating() || isInTournament(player) || isFollowMode();
    }

    public void checkForHotbarUpdate() {
        Player player = getPlayer();

        if (player == null) {
            return;
        }

        if (isInLobby() && !kitEditor.isActive()) {
            boolean update = false;

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());

                if (System.currentTimeMillis() - rematchData.getTimestamp() >= 15_000) {
                    rematchData = null;
                    update = true;
                } else if (target == null || !target.isOnline()) {
                    rematchData = null;
                    update = true;
                } else {
                    Profile profile = Profile.getByUuid(target.getUniqueId());

                    if (!(profile.isInLobby() || profile.isInQueue())) {
                        rematchData = null;
                        update = true;
                    } else if (this.getRematchData() == null) {
                        rematchData = null;
                        update = true;
                    } else if (!rematchData.getKey().equals(this.getRematchData().getKey())) {
                        rematchData = null;
                        update = true;
                    } else if (rematchData.isReceive()) {
                        ///int requestSlot = player.getInventory().first(Hotbar.getItems().get(HotbarItem.REMATCH_ACCEPT));

                        /*if (requestSlot != -1) {
                            update = true;
                        }*/
                    }
                }
            }

            {
                boolean activeEvent = (TechniquePlugin.get().getSumoManager().getActiveSumo() != null && TechniquePlugin.get().getSumoManager().getActiveSumo().isWaiting())
                        || (TechniquePlugin.get().getBracketsManager().getActiveBrackets() != null && TechniquePlugin.get().getBracketsManager().getActiveBrackets().isWaiting())
                        || (TechniquePlugin.get().getLMSManager().getActiveLMS() != null && TechniquePlugin.get().getLMSManager().getActiveLMS().isWaiting())
                        || (TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut() != null && TechniquePlugin.get().getJuggernautManager().getActiveJuggernaut().isWaiting())
                        || (TechniquePlugin.get().getParkourManager().getActiveParkour() != null && TechniquePlugin.get().getParkourManager().getActiveParkour().isWaiting())
                        || (TechniquePlugin.get().getWipeoutManager().getActiveWipeout() != null && TechniquePlugin.get().getWipeoutManager().getActiveWipeout().isWaiting())
                        || (TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars() != null && TechniquePlugin.get().getSkyWarsManager().getActiveSkyWars().isWaiting())
                        || (TechniquePlugin.get().getSpleefManager().getActiveSpleef() != null && TechniquePlugin.get().getSpleefManager().getActiveSpleef().isWaiting())
                        || (TechniquePlugin.get().getInfectedManager().getActiveInfected() != null && TechniquePlugin.get().getInfectedManager().getActiveInfected().isWaiting());
                int eventSlot = player.getInventory().first(Hotbar.getItems().get(HotbarItem.EVENT_JOIN));

                if (eventSlot == -1 && activeEvent) {
                    update = true;
                } else if (eventSlot != -1 && !activeEvent) {
                    update = true;
                }
            }

            if (update) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        refreshHotbar();
                    }
                }.runTask(TechniquePlugin.get());
            }
        }
    }

    public void refreshHotbar() {
        Player player = getPlayer();

        if (player != null) {
            PlayerUtil.reset(player, false);

            if (isInLobby()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.LOBBY, this));
            } else if (isInQueue()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.QUEUE, this));
            } else if (isInFFA()) {
                FFA.assignKit(player);
            } else if (isSpectating()) {
                PlayerUtil.spectator(player);
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, this));
            } else if (isInSumo()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SUMO_SPECTATE, this));
            } else if (isInBrackets()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.BRACKETS_SPECTATE, this));
            } else if (isInLMS()) {
                if (getLms().getEventPlayer(player).getState().equals(LMSPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.FFA_SPECTATE, this));
            } else if (isInJuggernaut()) {
                if (getJuggernaut().getEventPlayer(player).getState().equals(JuggernautPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.JUGGERNAUT_SPECTATE, this));
            } else if (isInParkour()) {
                if (getParkour().getEventPlayer(player).getState().equals(ParkourPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.PARKOUR_SPECTATE, this));
            } else if (isInWipeout()) {
                if (getWipeout().getEventPlayer(player).getState().equals(WipeoutPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.WIPEOUT_SPECTATE, this));
            } else if (isInSkyWars()) {
                if (getSkyWars().getEventPlayer(player).getState().equals(SkyWarsPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SKYWARS_SPECTATE, this));
            } else if (isInSpleef()) {
                if (getSpleef().getEventPlayer(player).getState().equals(SpleefPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SPLEEF_SPECTATE, this));
            } else if (isInInfected()) {
                if (getInfected().getEventPlayer(player).getState().equals(InfectedPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.INFECTED_SPECTATE, this));
            } else if (isInFight()) {
                if (!match.getTeamPlayer(player).isAlive()) {
                    player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, this));
                }
            }

            player.updateInventory();
        }
    }

    public void handleVisibility(Player player, Player otherPlayer) {
        if (player == null || otherPlayer == null) return;

        boolean hide = true;

        if (state == ProfileState.IN_LOBBY || state == ProfileState.IN_QUEUE) {
            hide = !visibility;
            hide = false;
            if (visibility) {
                /*if (!player.hasPermission("technique.seespawnplayers")) {
                    Profile oProfile = getByUuid(otherPlayer);
                    if (oProfile.isSilent()) {
                        hide = true;
                    } else {
                        if (!(AquaCoreAPI.INSTANCE.getPlayerRank(otherPlayer.getUniqueId()).getWeight() > 4)) {
                            hide = true;
                        }
                    }
                }*/
                hide = false;
            }

            if (party != null && party.containsPlayer(otherPlayer)) {
                hide = false;
                //NameTags.color(player, otherPlayer, ChatColor.BLUE, false);
            } else {
                Clan clan = Clan.getByMember(player.getUniqueId());
                if (clan != null && clan.getClanPlayer(otherPlayer.getUniqueId()) != null) {
                    //String shortenedClanname = StringUtils.left(clan.getName(), 4);
                    //String string = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + shortenedClanname + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
                    //NameTags.color(player, otherPlayer, string, "", false);
                    hide = false;
                }
            }
        } else if (isInFight()) {
            TeamPlayer teamPlayer = match.getTeamPlayer(otherPlayer);

            if (teamPlayer != null && teamPlayer.isAlive()) {
                hide = false;
            }
        } else if (isSpectating()) {
            if (sumo != null) {
                SumoPlayer sumoPlayer = sumo.getEventPlayer(otherPlayer);
                if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayerState.WAITING) {
                    hide = false;
                }
            } else if (brackets != null) {
                BracketsPlayer bracketsPlayer = brackets.getEventPlayer(otherPlayer);
                if (bracketsPlayer != null && bracketsPlayer.getState() == BracketsPlayerState.WAITING) {
                    hide = false;
                }
            } else if (lms != null) {
                LMSPlayer LMSPlayer = lms.getEventPlayer(otherPlayer);
                if (LMSPlayer != null && LMSPlayer.getState() == LMSPlayerState.WAITING) {
                    hide = false;
                }
            } else if (juggernaut != null) {
                JuggernautPlayer juggernautPlayer = juggernaut.getEventPlayer(otherPlayer);
                if (juggernautPlayer != null && juggernautPlayer.getState() == JuggernautPlayerState.WAITING) {
                    hide = false;
                }
            } else if (parkour != null) {
                ParkourPlayer parkourPlayer = parkour.getEventPlayer(otherPlayer);
                if (parkourPlayer != null && parkourPlayer.getState() == ParkourPlayerState.WAITING) {
                    hide = false;
                }
            } else if (wipeout != null) {
                WipeoutPlayer wipeoutPlayer = wipeout.getEventPlayer(otherPlayer);
                if (wipeoutPlayer != null && wipeoutPlayer.getState() == WipeoutPlayerState.WAITING) {
                    hide = false;
                }
            } else if (skyWars != null) {
                SkyWarsPlayer skyWarsPlayer = skyWars.getEventPlayer(otherPlayer);
                if (skyWarsPlayer != null && skyWarsPlayer.getState() == SkyWarsPlayerState.WAITING) {
                    hide = false;
                }
            } else if (spleef != null) {
                SpleefPlayer spleefPlayer = spleef.getEventPlayer(otherPlayer);
                if (spleefPlayer != null && spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                    hide = false;
                }
            } else if (infected != null) {
                InfectedPlayer infectedPlayer = infected.getEventPlayer(otherPlayer);
                if (infectedPlayer != null && (infectedPlayer.getState() == InfectedPlayerState.WAITING || infectedPlayer.getState() == InfectedPlayerState.INFECTED)) {
                    hide = false;
                }
            } else {
                TeamPlayer teamPlayer = match.getTeamPlayer(otherPlayer);
                if (teamPlayer != null && teamPlayer.isAlive()) {
                    hide = false;
                }
            }
        } else if (isInEvent()) {
            if (sumo != null) {
                if (!sumo.getSpectators().contains(otherPlayer.getUniqueId())) {
                    SumoPlayer sumoPlayer = sumo.getEventPlayer(otherPlayer);
                    if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayerState.WAITING) {
                        hide = false;
                    }
                }
            } else if (brackets != null) {
                BracketsPlayer bracketsPlayer = brackets.getEventPlayer(otherPlayer);
                if (bracketsPlayer != null && bracketsPlayer.getState() == BracketsPlayerState.WAITING) {
                    hide = false;
                }
            } else if (lms != null) {
                LMSPlayer LMSPlayer = lms.getEventPlayer(otherPlayer);
                if (LMSPlayer != null && LMSPlayer.getState() == LMSPlayerState.WAITING) {
                    hide = false;
                }
            } else if (juggernaut != null) {
                JuggernautPlayer juggernautPlayer = juggernaut.getEventPlayer(otherPlayer);
                if (juggernautPlayer != null && juggernautPlayer.getState() == JuggernautPlayerState.WAITING) {
                    hide = false;
                }
            } else if (parkour != null) {
                ParkourPlayer parkourPlayer = parkour.getEventPlayer(otherPlayer);
                if (parkourPlayer != null && parkourPlayer.getState() == ParkourPlayerState.WAITING) {
                    hide = false;
                }
            } else if (wipeout != null) {
                WipeoutPlayer wipeoutPlayer = wipeout.getEventPlayer(otherPlayer);
                if (wipeoutPlayer != null && wipeoutPlayer.getState() == WipeoutPlayerState.WAITING) {
                    hide = false;
                }
            } else if (skyWars != null) {
                SkyWarsPlayer skyWarsPlayer = skyWars.getEventPlayer(otherPlayer);
                if (skyWarsPlayer != null && skyWarsPlayer.getState() == SkyWarsPlayerState.WAITING) {
                    hide = false;
                }
            } else if (spleef != null) {
                SpleefPlayer spleefPlayer = spleef.getEventPlayer(otherPlayer);
                if (spleefPlayer != null && spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                    hide = false;
                }
            } else if (infected != null) {
                InfectedPlayer infectedPlayer = infected.getEventPlayer(otherPlayer);
                if (infectedPlayer != null && (infectedPlayer.getState() == InfectedPlayerState.WAITING || infectedPlayer.getState() == InfectedPlayerState.INFECTED)) {
                    hide = false;
                }
            }

        }

        if (hide) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.hidePlayer(otherPlayer);
                }
            }.runTask(TechniquePlugin.get());
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.showPlayer(otherPlayer);
                }
            }.runTask(TechniquePlugin.get());
        }
    }

    public void handleVisibility() {
        Player player = getPlayer();
        if (Bukkit.getPluginManager().getPlugin("SmokDisguise") != null) {
            if (DisguiseHook.isPlayerDisguised(player)) {
                player = NickPlugin.getPlugin().getAPI().getPlayerOfNickedName(NickPlugin.getPlugin().getAPI().getNickedName(player));
            }
        }

        if (player != null) {
            Player finalPlayer = player;
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                        handleVisibility(finalPlayer, otherPlayer);
                    }
                }
            }.runTaskAsynchronously(TechniquePlugin.get());
        }
    }

    public void setEnderpearlCooldown(Cooldown cooldown) {
        this.enderpearlCooldown = cooldown;

        try {
            final Player player = this.getPlayer();
            if (player != null) {
                //scha45: LunarClientAPI.getInstance().sendCooldown(player, new LCCooldown("EnderPearl", cooldown.getDuration(), TimeUnit.MILLISECONDS, Material.ENDER_PEARL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
