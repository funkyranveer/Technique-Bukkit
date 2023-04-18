package scha.efer.technique;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.qrakn.honcho.Honcho;
import com.qrakn.phoenix.lang.file.type.BasicConfigurationFile;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;
import scha.efer.technique.arena.Arena;
import scha.efer.technique.arena.ArenaType;
import scha.efer.technique.arena.ArenaTypeAdapter;
import scha.efer.technique.arena.ArenaTypeTypeAdapter;
import scha.efer.technique.arena.command.*;
import scha.efer.technique.arena.selection.ArenaSelectionListener;
import scha.efer.technique.arena.selection.GoldenHeads;
import scha.efer.technique.clan.Clan;
import scha.efer.technique.clan.ClanListener;
import scha.efer.technique.clan.command.*;
import scha.efer.technique.commands.RefillCommand;
import scha.efer.technique.commands.SetEloCommand;
import scha.efer.technique.commands.UpdateCommand;
import scha.efer.technique.duel.command.DuelAcceptCommand;
import scha.efer.technique.duel.command.DuelCommand;
import scha.efer.technique.duel.command.RematchCommand;
import scha.efer.technique.event.EventCommand;
import scha.efer.technique.event.impl.brackets.BracketsListener;
import scha.efer.technique.event.impl.brackets.BracketsManager;
import scha.efer.technique.event.impl.brackets.command.*;
import scha.efer.technique.event.impl.infected.InfectedListener;
import scha.efer.technique.event.impl.infected.InfectedManager;
import scha.efer.technique.event.impl.juggernaut.JuggernautListener;
import scha.efer.technique.event.impl.juggernaut.JuggernautManager;
import scha.efer.technique.event.impl.lms.LMSListener;
import scha.efer.technique.event.impl.lms.LMSManager;
import scha.efer.technique.event.impl.lms.command.*;
import scha.efer.technique.event.impl.parkour.ParkourListener;
import scha.efer.technique.event.impl.parkour.ParkourManager;
import scha.efer.technique.event.impl.parkour.command.*;
import scha.efer.technique.event.impl.skywars.SkyWarsListener;
import scha.efer.technique.event.impl.skywars.SkyWarsManager;
import scha.efer.technique.event.impl.skywars.command.*;
import scha.efer.technique.event.impl.spleef.SpleefListener;
import scha.efer.technique.event.impl.spleef.SpleefManager;
import scha.efer.technique.event.impl.spleef.command.*;
import scha.efer.technique.event.impl.sumo.SumoListener;
import scha.efer.technique.event.impl.sumo.SumoManager;
import scha.efer.technique.event.impl.sumo.command.*;
import scha.efer.technique.event.impl.tournament.command.*;
import scha.efer.technique.event.impl.wipeout.WipeoutListener;
import scha.efer.technique.event.impl.wipeout.WipeoutManager;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.kit.KitEditorListener;
import scha.efer.technique.kit.KitTypeAdapter;
import scha.efer.technique.kit.command.*;
import scha.efer.technique.match.Match;
import scha.efer.technique.match.MatchListener;
import scha.efer.technique.match.command.SpectateCommand;
import scha.efer.technique.match.command.StopSpectatingCommand;
import scha.efer.technique.match.command.ViewInventoryCommand;
import scha.efer.technique.match.kits.utils.ArmorClassManager;
import scha.efer.technique.match.kits.utils.bard.EffectRestorer;
import scha.efer.technique.party.Party;
import scha.efer.technique.party.PartyListener;
import scha.efer.technique.party.command.*;
import scha.efer.technique.placeholders.PlaceholderAPIExtension;
import scha.efer.technique.profile.Profile;
import scha.efer.technique.profile.ProfileListener;
import scha.efer.technique.profile.command.*;
import scha.efer.technique.profile.command.donator.FlyCommand;
import scha.efer.technique.profile.command.donator.ToggleVisibilityCommand;
import scha.efer.technique.profile.command.staff.*;
import scha.efer.technique.profile.hotbar.Hotbar;
import scha.efer.technique.profile.hotbar.HotbarListener;
import scha.efer.technique.profile.stats.command.LeaderboardsCommand;
import scha.efer.technique.profile.stats.command.StatsCommand;
import scha.efer.technique.queue.QueueListener;
import scha.efer.technique.queue.QueueThread;
import scha.efer.technique.scoreboard.Assemble;
import scha.efer.technique.scoreboard.adapter.ScoreboardAdapter;
import scha.efer.technique.util.EntityHider;
import scha.efer.technique.util.InventoryUtil;
import scha.efer.technique.util.SmokHook;
import scha.efer.technique.util.essentials.Essentials;
import scha.efer.technique.util.essentials.listener.EssentialsListener;
import scha.efer.technique.util.events.ArmorListener;
import scha.efer.technique.util.events.WorldListener;
import scha.efer.technique.util.external.duration.Duration;
import scha.efer.technique.util.external.duration.DurationTypeAdapter;
import scha.efer.technique.util.external.menu.MenuListener;

import java.util.Arrays;

public class TechniquePlugin extends JavaPlugin {

    private static TechniquePlugin Practice;

    @Getter
    private BasicConfigurationFile mainConfig;
    @Getter
    private BasicConfigurationFile arenasConfig;
    @Getter
    private BasicConfigurationFile kitsConfig;
    @Getter
    private BasicConfigurationFile eventsConfig;
    @Getter
    private BasicConfigurationFile chestsConfig;

    @Getter
    private MongoDatabase mongoDatabase;

    @Getter
    private SumoManager sumoManager;
    @Getter
    private BracketsManager bracketsManager;
    @Getter
    private scha.efer.technique.event.impl.lms.LMSManager LMSManager;
    @Getter
    private JuggernautManager juggernautManager;
    @Getter
    private ParkourManager parkourManager;
    @Getter
    private WipeoutManager wipeoutManager;
    @Getter
    private SkyWarsManager skyWarsManager;
    @Getter
    private SpleefManager spleefManager;
    @Getter
    private InfectedManager infectedManager;

    @Getter
    private ArmorClassManager armorClassManager;

    @Getter
    private EffectRestorer effectRestorer;

    @Getter
    private Essentials essentials;

    @Getter
    private SmokHook smokHook;

    @Getter
    private Honcho honcho;

    @Getter
    private Assemble scoreboard;

    @Getter
    private boolean disabling = false;

    @Getter
    private EntityHider entityHider;

    public static TechniquePlugin get() {
        return Practice;
    }

    @Override
    public void onEnable() {
        Practice = this;

        honcho = new Honcho(this);

        mainConfig = new BasicConfigurationFile(this, "config");
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        kitsConfig = new BasicConfigurationFile(this, "kits");
        eventsConfig = new BasicConfigurationFile(this, "events");
        chestsConfig = new BasicConfigurationFile(this, "chests");

        loadMongo();

        Profile.init();
        Kit.init();
        Arena.init();
        Hotbar.init();
        Match.init();
        Party.init();
        Clan.init();

        essentials = new Essentials(this);
        smokHook = new SmokHook();

        honcho.registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
        honcho.registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
        honcho.registerTypeAdapter(Kit.class, new KitTypeAdapter());

        for (Object command : Arrays.asList(
                //Staff commands
                new SetSpawnCommand(),
                new RemoveProfileCommand(),
                new SilentCommand(),
                new FollowCommand(),
                new UnFollowCommand(),
                new GetLocationCommand(),

                //Player commands
                new TsbCommand(),
                new TduelCommand(),
                new TpmCommand(),
                new StatsCommand(),
                new LeaderboardsCommand(),
                new OptionsCommand(),
                new EventCommand(),
                new PracticeCommand(),

                //Donator commands
                new FlyCommand(),
                new ToggleVisibilityCommand(),

                //practice commands
                new RefillCommand(),
                new SetEloCommand(),
                new UpdateCommand(),


                //Arena commands
                new ArenaAddKitCommand(),
                new ArenaRemoveKitCommand(),
                new ArenaSetSpawnCommand(),
                new ArenaSetBedCommand(),
                new ArenaSetPointCommand(),
                new ArenaCreateCommand(),
                new ArenaGoldenHeadCommand(),
                new ArenaRemoveCommand(),
                new ArenasCommand(),
                new ArenaSetPortalCommand(),
                new ArenaSetIconCommand(),
                new ArenaPortalWandCommand(),
                new ArenaSetClaimCommand(),
                new ArenaTpCommand(),

                //Duel commands
                new DuelCommand(),
                new DuelAcceptCommand(),
                new RematchCommand(),
                new ViewInventoryCommand(),
                new SpectateCommand(),
                new StopSpectatingCommand(),

                //Clan command
                new ClanCommand(),
                new ClanDisbandCommand(),
                new ClanCreateCommand(),
                new ClanInfoCommand(),
                new ClanInviteCommand(),
                new ClanJoinCommand(),
                new ClanKickCommand(),
                new ClanLeaveCommand(),
                new ClanShowInvCommand(),
                new ClanCaptainCommand(),
                new ClanDemoteCommand(),
                new ClanResetCommand(),
                new ClanDescCommand(),
                new ClanRenameCommand(),
                new ClanForceJoinCommand(),
                new ClanForceLeaderCommand(),

                //Party command
                new PartyCloseCommand(),
                new PartyCreateCommand(),
                new PartyDisbandCommand(),
                new PartyHelpCommand(),
                new PartyInfoCommand(),
                new PartyInviteCommand(),
                new PartyJoinCommand(),
                new PartyKickCommand(),
                new PartyLeaveCommand(),
                new PartyOpenCommand(),
                new PartyLeaderCommand(),
                new PartyUnbanCommand(),
                new PartyBanCommand(),

                //Kit command
                new KitCreateCommand(),
                new KitGetLoadoutCommand(),
                new KitSetLoadoutCommand(),
                new KitSetKnockbackProfileCommand(),
                new KitBridgeCommand(),
                new KitSetSlotUnrankedCommand(),
                new KitSetSlotRankedCommand(),
                new KitListCommand(),

                //Brackets command
                new BracketsLeaveCommand(),
                new BracketsCancelCommand(),
                new BracketsCooldownCommand(),
                new BracketsJoinCommand(),
                new BracketsSetSpawnCommand(),
                new BracketsHostCommand(),
                new BracketsTpCommand(),
                new BracketsHelpCommand(),

                //Sumo command
                new SumoCancelCommand(),
                new SumoCooldownCommand(),
                new SumoHostCommand(),
                new SumoJoinCommand(),
                new SumoLeaveCommand(),
                new SumoSetSpawnCommand(),
                new SumoTpCommand(),
                new SumoHelpCommand(),

                //LMS command
                new LMSCancelCommand(),
                new LMSCooldownCommand(),
                new LMSHostCommand(),
                new LMSJoinCommand(),
                new LMSLeaveCommand(),
                new LMSSetSpawnCommand(),
                new LMSTpCommand(),
                new LMSHelpCommand(),

                //Juggernaut command
/*                new JuggernautCancelCommand(),
                new JuggernautCooldownCommand(),
                new JuggernautHostCommand(),
                new JuggernautJoinCommand(),
                new JuggernautLeaveCommand(),
                new JuggernautSetSpawnCommand(),
                new JuggernautTpCommand(),*/

                //Parkour command
                new ParkourCancelCommand(),
                new ParkourCooldownCommand(),
                new ParkourHostCommand(),
                new ParkourJoinCommand(),
                new ParkourLeaveCommand(),
                new ParkourSetSpawnCommand(),
                new ParkourTpCommand(),
                new ParkourHelpCommand(),

                //Wipeout command
/*                new WipeoutCancelCommand(),
                new WipeoutCooldownCommand(),
                new WipeoutHostCommand(),
                new WipeoutJoinCommand(),
                new WipeoutLeaveCommand(),
                new WipeoutSetSpawnCommand(),
                new WipeoutTpCommand(),*/

                //SkyWars command
                new SkyWarsCancelCommand(),
                new SkyWarsCooldownCommand(),
                new SkyWarsHostCommand(),
                new SkyWarsJoinCommand(),
                new SkyWarsLeaveCommand(),
                new SkyWarsSetSpawnCommand(),
                new SkyWarsTpCommand(),
                new SkyWarsSetChestCommand(),
                new SkyWarsHelpCommand(),

                //Spleef command
                new SpleefCancelCommand(),
                new SpleefCooldownCommand(),
                new SpleefHostCommand(),
                new SpleefJoinCommand(),
                new SpleefLeaveCommand(),
                new SpleefSetSpawnCommand(),
                new SpleefTpCommand(),
                new SpleefHelpCommand(),

                //Infected command
/*                new InfectedCancelCommand(),
                new InfectedCooldownCommand(),
                new InfectedHostCommand(),
                new InfectedJoinCommand(),
                new InfectedLeaveCommand(),
                new InfectedSetSpawnCommand(),
                new InfectedTpCommand(),*/

                //Tournament command
                new TournamentCommand(),
                new TournamentLeaveCommand(),
                new TournamentJoinCommand(),
                new TournamentHostCommand(),
                new TournamentCancelCommand()
        )) {
            honcho.registerCommand(command);
        }

        honcho.registerTypeAdapter(Duration.class, new DurationTypeAdapter());

        sumoManager = new SumoManager();
        bracketsManager = new BracketsManager();
        LMSManager = new LMSManager();
        juggernautManager = new JuggernautManager();
        parkourManager = new ParkourManager();
        wipeoutManager = new WipeoutManager();
        skyWarsManager = new SkyWarsManager();
        spleefManager = new SpleefManager();
        infectedManager = new InfectedManager();

        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        this.effectRestorer = new EffectRestorer(this);
        this.armorClassManager = new ArmorClassManager(this);

        Arrays.asList(
                Material.WORKBENCH,
                Material.STICK,
                Material.WOOD_PLATE,
                Material.WOOD_BUTTON,
                Material.SNOW_BLOCK
        ).forEach(InventoryUtil::removeCrafting);

        getServer().getWorlds().forEach(world -> {
            world.setDifficulty(Difficulty.HARD);
            essentials.clearEntities(world);
        });

        for (Listener listener : Arrays.asList(
                new ProfileListener(),
                new MenuListener(this),
                new EssentialsListener(this),
                new GoldenHeads(this),
                new SumoListener(),
                new BracketsListener(),
                new LMSListener(),
                new ArenaSelectionListener(),
                new JuggernautListener(),
                new ParkourListener(),
                new WipeoutListener(),
                new SkyWarsListener(),
                new SpleefListener(),
                new InfectedListener(),
                new KitEditorListener(),
                new PartyListener(),
                new HotbarListener(),
                new MatchListener(),
                new WorldListener(),
                new QueueListener(),
                new ArmorListener(),
                new ClanListener()
        )) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        /*Ziggurat zig = new Ziggurat(this, new TabAdapter());
        zig.setHook(true);*/

        scoreboard = new Assemble(this, new ScoreboardAdapter(this));

        new QueueThread().start();

        new PlaceholderAPIExtension().register();

        //SkyWarsChest.loadAll();
    }

    public MetadataValue getMetadata(Metadatable m, String tag) {
        for (MetadataValue mv : m.getMetadata(tag))
            if (mv != null && mv.getOwningPlugin() != null && mv.getOwningPlugin() == this) {
                return mv;
            }
        return null;
    }

    @Override
    public void onDisable() {
        disabling = true;
        entityHider.close();
        Match.cleanup();
        for (Profile profile : Profile.getProfiles().values()) {
            profile.save();
        }
    }

    private void loadMongo() {
        if (mainConfig.getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
            mongoDatabase = new MongoClient(
                    new ServerAddress(
                            mainConfig.getString("MONGO.HOST"),
                            mainConfig.getInteger("MONGO.PORT")
                    ),
                    MongoCredential.createCredential(
                            mainConfig.getString("MONGO.AUTHENTICATION.USERNAME"),
                            mainConfig.getString("MONGO.AUTHENTICATION.DATABASE"), mainConfig.getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()
                    ),
                    MongoClientOptions.builder().build()
            ).getDatabase(mainConfig.getString("MONGO.DATABASE"));
        } else {
            mongoDatabase = new MongoClient(mainConfig.getString("MONGO.HOST"), mainConfig.getInteger("MONGO.PORT"))
                    .getDatabase(mainConfig.getString("MONGO.DATABASE"));
        }
    }
}
