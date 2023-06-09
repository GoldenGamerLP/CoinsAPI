package me.alex.coinsapi.implementation;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.ChameleonPlugin;
import dev.hypera.chameleon.annotations.Plugin;
import dev.hypera.chameleon.platform.Platform;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.alex.coinsapi.api.CoinAPI;
import me.alex.coinsapi.api.CoinAPIProvider;
import me.alex.coinsapi.api.CoinUserDAO;
import me.alex.coinsapi.api.CoinsUserCache;
import me.alex.coinsapi.implementation.commands.CoinsCommand;
import me.alex.coinsapi.implementation.data.Messages;
import me.alex.coinsapi.implementation.data.MongoDBImpl;
import me.alex.coinsapi.implementation.data.configuration.DefaultConfiguration;
import me.alex.coinsapi.implementation.event.PlayerLeaveEvent;
import me.alex.coinsapi.implementation.event.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Plugin(id = "coinsapi",
        name = "CoinsAPI",
        version = "@version@",
        authors = "Alex",
        description = "CoinsAPI",
        platforms = {
                Platform.BUKKIT,
                Platform.BUNGEECORD,
                Platform.VELOCITY,
                Platform.MINESTOM
        })
@Slf4j
public class CoinsAPI extends ChameleonPlugin {

    @Getter
    private final Instant start = Instant.now();
    @Getter
    private Chameleon chameleon;
    @Getter
    private DefaultConfiguration configuration;
    @Getter
    private MongoDBImpl database;
    @Getter
    private CoinAPI api;


    public CoinsAPI(@NotNull Chameleon chameleon) {
        super(chameleon);
        this.chameleon = chameleon;
    }

    @Override
    public void onLoad() {
        log.info("Loading Configuration...");
        this.configuration = new DefaultConfiguration(this);
        this.configuration.load();
        log.info("Configuration loaded! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
        log.info("Loading messages...");
        Messages.innit();
        log.info("Loaded messages! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
        log.info("Connecting to database...");
        this.database = new MongoDBImpl(this);
        this.database.connect();
        log.info("Connected to database! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
    }

    @Override
    public void onEnable() {
        log.info("Enabling CoinsAPI and registering events");
        new PlayerLoginEvent(this);
        new PlayerLeaveEvent(this);

        chameleon.getCommandManager().register(new CoinsCommand(this));

        log.info("Initializing API");
        initAPI();
        log.info("CoinsAPI enabled! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
    }

    @Override
    public void onDisable() {
        log.info("Disabling CoinsAPI");
        this.api.getCache().invalidateAll();
        log.info("Disconnected from database");
        this.database.disconnect();
        log.info("CoinsAPI disabled");
    }

    private void initAPI() {
        api = new CoinAPI() {
            @Override
            public CoinsUserCache getCache() {
                return database;
            }

            @Override
            public CoinUserDAO getDatabase() {
                return database;
            }
        };
        CoinAPIProvider.setInstance(api);
    }

    public CoinsUserCache getCache() {
        return database;
    }
}
