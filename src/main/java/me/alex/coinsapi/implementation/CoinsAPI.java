package me.alex.coinsapi.implementation;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.ChameleonPlugin;
import dev.hypera.chameleon.annotations.Plugin;
import dev.hypera.chameleon.platform.Platform;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.alex.coinsapi.api.CoinAPI;
import me.alex.coinsapi.api.CoinAPIProvider;
import me.alex.coinsapi.implementation.data.MongoDBImpl;
import me.alex.coinsapi.implementation.data.UserCache;
import me.alex.coinsapi.implementation.data.configuration.DefaultConfiguration;
import me.alex.coinsapi.implementation.event.PlayerLeaveEvent;
import me.alex.coinsapi.implementation.event.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

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
    private Chameleon chameleon;
    @Getter
    private DefaultConfiguration configuration;
    @Getter
    private MongoDBImpl database;
    @Getter
    private UserCache userCache;

    public CoinsAPI(@NotNull Chameleon chameleon) {
        super(chameleon);

        this.chameleon = chameleon;
    }

    @Override
    public void onLoad() {
        log.info("Loading CoinsAPI");
        this.configuration = new DefaultConfiguration(this);

        this.configuration.load();
        log.info("Connecting to database");
        this.database = new MongoDBImpl(this);
        this.database.connect();
    }

    @Override
    public void onEnable() {
        log.info("Enabling CoinsAPI");
        new PlayerLoginEvent(this);

        boolean useCache = configuration
                .getConfiguration()
                .getBoolean("use_cache");

        if (useCache) {
            this.userCache = new UserCache(this);
            new PlayerLeaveEvent(this);
        }

        log.info("Initializing API");
        initAPI(useCache);
    }

    @Override
    public void onDisable() {
        this.database.disconnect();
    }

    private void initAPI(boolean useCache) {
        CoinAPI api = () -> useCache ? userCache : database;
        CoinAPIProvider.setInstance(api);
    }
}
