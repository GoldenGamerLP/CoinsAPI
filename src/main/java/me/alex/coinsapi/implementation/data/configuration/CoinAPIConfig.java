package me.alex.coinsapi.implementation.data.configuration;

import com.typesafe.config.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.http.aeon.annotations.Options;

@Getter
@Options(path = "coins-config",name = "coinsapi")
public final class CoinAPIConfig {

    private final String database_mongodb_uri;
    private final String database_mongodb_database;
    private final String database_mongodb_collection;
    private final Integer threads_threshold;
    private final Boolean commands_enabled;

    public CoinAPIConfig() {
        this.database_mongodb_uri = "mongodb://localhost:27017";
        this.database_mongodb_database = "coins";
        this.database_mongodb_collection = "users";
        this.threads_threshold = 1;
        this.commands_enabled = true;
    }
}
