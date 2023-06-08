package me.alex.coinsapi.implementation;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.ChameleonPlugin;
import dev.hypera.chameleon.annotations.Plugin;
import dev.hypera.chameleon.logger.ChameleonLogger;
import dev.hypera.chameleon.platform.Platform;
import lombok.Getter;
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
    private ChameleonLogger logger;
    @Getter
    private CoinAPI api;

    public CoinsAPI(@NotNull Chameleon chameleon) {
        super(chameleon);
        this.chameleon = chameleon;
    }

    @Override
    public void onLoad() {
        this.logger = chameleon.getLogger();
        logger.info("Loading Configuration...");
        this.configuration = new DefaultConfiguration(this);
        this.configuration.load();
        logger.info("Configuration loaded! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
        logger.info("Loading messages...");
        Messages.innit();
        logger.info("Loaded messages! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
        logger.info("Connecting to database...");
        this.database = new MongoDBImpl(this);
        this.database.connect();
        logger.info("Connected to database! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
    }

    @Override
    public void onEnable() {
        logger.info("Enabling CoinsAPI and registering events");
        new PlayerLoginEvent(this);
        new PlayerLeaveEvent(this);

        chameleon.getCommandManager().register(new CoinsCommand(this));

        logger.info("Initializing API");
        initAPI();
        logger.info("CoinsAPI enabled! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
    }

    @Override
    public void onDisable() {
        logger.info("Disabling CoinsAPI");
        this.api.getCache().invalidateAll();
        logger.info("Disconnected from database");
        this.database.disconnect();
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
}
