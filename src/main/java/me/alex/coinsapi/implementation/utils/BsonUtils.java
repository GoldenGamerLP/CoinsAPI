package me.alex.coinsapi.implementation.utils;

import com.mongodb.client.model.Filters;
import me.alex.coinsapi.api.CoinUser;
import me.alex.coinsapi.implementation.data.pojo.UserImpl;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BsonUtils {

    //Convert CoinUser to BsonDocument
    public static Document toBson(@NotNull CoinUser user) {
        return new Document()
                .append("uuid", user.getUniqueId().toString())
                .append("coins", user.getCoins())
                .append("lastKnownName", user.getLastKnownName())
                .append("multiplier", user.getMultiplier());
    }

    //Convert BsonDocument to CoinUser
    @NotNull
    @Contract("_ -> new")
    public static CoinUser fromBson(@NotNull BsonDocument document) {
        return new UserImpl(
                UUID.fromString(document.get("uuid").asString().getValue()),
                document.get("coins").asNumber().longValue(),
                document.get("lastKnownName").asString().getValue(),
                document.get("multiplier").asDouble().getValue());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Bson filterForUUID(UUID uuid) {
        return Filters.eq("uuid", uuid.toString());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Bson filterForName(String name) {
        return Filters.eq("lastKnownName", name);
    }
}
