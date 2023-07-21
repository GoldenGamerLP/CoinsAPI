package me.alex.coinsapi.implementation;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.ChameleonPlugin;
import dev.hypera.chameleon.annotations.Plugin;
import dev.hypera.chameleon.platform.Platform;
import lombok.Getter;
import me.alex.coinsapi.api.CoinAPI;
import me.alex.coinsapi.api.CoinAPIProvider;
import me.alex.coinsapi.api.CoinUserDAO;
import me.alex.coinsapi.api.CoinsUserCache;
import me.alex.coinsapi.implementation.commands.CoinsCommand;
import me.alex.coinsapi.implementation.data.Messages;
import me.alex.coinsapi.implementation.data.MongoDBImpl;
import me.alex.coinsapi.implementation.data.configuration.CoinAPIConfig;
import me.alex.coinsapi.implementation.data.configuration.DefaultConfiguration;
import me.alex.coinsapi.implementation.event.PlayerLeaveEvent;
import me.alex.coinsapi.implementation.event.PlayerLoginEvent;
import me.alex.coinsapi.implementation.threads.CoinsExecutor;
import net.http.aeon.Aeon;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

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
@Getter
public class CoinsAPI extends ChameleonPlugin {

    private Instant start = Instant.now();
    private final Logger logger = LoggerFactory.getLogger(CoinsAPI.class);
    private final Chameleon chameleon;
    private DefaultConfiguration configuration;
    private MongoDBImpl database;
    private CoinAPI api;
    private CoinsExecutor executor;
    private CoinAPIConfig config;


    public CoinsAPI(@NotNull Chameleon chameleon) {
        super(chameleon);
        this.chameleon = chameleon;
    }

    @Override
    public void onLoad() {
        logger.info("CoinsAPI loading...");
        //None depended initializations
        this.config = Aeon.insert(new CoinAPIConfig(),this.chameleon.getDataFolder());
        this.executor = new CoinsExecutor(this);
        logger.info("Loaded CoinsAPI! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
    }

    @Override
    public void onEnable() {
        start = Instant.now();
        logger.info("CoinsAPI enabling...");
        this.database = new MongoDBImpl(this);
        new PlayerLoginEvent(this);
        new PlayerLeaveEvent(this);

        //Simultaneous initializations
        initAsync();

        boolean isCommandsEnabled = getConfig().getCommands_enabled();
        if (isCommandsEnabled) chameleon.getCommandManager().register(new CoinsCommand(this));

        //Dont put this async because of dependencies and other plugins
        initAPI();
        logger.info("CoinsAPI Enabled! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
    }

    @Override
    public void onDisable() {
        logger.info("Disabling CoinsAPI");
        this.api.getCache().invalidateAll();
        logger.info("Disconnected from database");
        this.database.disconnect();
        logger.info("CoinsAPI disabled");
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

    private void initAsync() {
        CompletableFuture<Void> message = CompletableFuture.runAsync(() -> Messages.innit(this), executor.getExecutor());
        CompletableFuture<Void> database = CompletableFuture.runAsync(() -> this.database.connect(), executor.getExecutor());
        CompletableFuture.allOf(message,database).whenComplete((unused, throwable) -> {
           if(throwable != null) {
               logger.error("An error occurred while initializing CoinsAPI",throwable);
               return;
           }
            logger.info("CoinsAPI initialized! Took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
        });
    }

    public CoinsUserCache getCache() {
        return database;
    }
}
