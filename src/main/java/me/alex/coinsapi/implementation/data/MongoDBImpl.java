package me.alex.coinsapi.implementation.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import me.alex.coinsapi.api.CoinUser;
import me.alex.coinsapi.api.CoinUserDAO;
import me.alex.coinsapi.api.CoinsUserCache;
import me.alex.coinsapi.implementation.CoinsAPI;
import me.alex.coinsapi.implementation.utils.BsonUtils;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "CoinsAPI")

public class MongoDBImpl implements CoinUserDAO, CoinsUserCache {

    private final CoinsAPI plugin;
    private final Map<UUID, CoinUser> cache;
    private MongoClient client;
    private MongoCollection<Document> collection;
    private final Executor executor;


    public MongoDBImpl(CoinsAPI plugin) {
        this.plugin = plugin;
        this.executor = plugin.getExecutor().getExecutor();
        this.cache = new ConcurrentHashMap<>();
    }

    @NotNull
    private MongoClientSettings getClientSettings(CoinsAPI plugin) {
        final MongoClientSettings settings;
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .deprecationErrors(true)
                .build();

        String uri = plugin.getConfig().getDatabase_mongodb_uri();

        settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();
        return settings;
    }

    @Override
    public void connect() {
        try {
            client = MongoClients.create(getClientSettings(plugin));
        } catch (Exception e) {
            log.error("Error while connecting to MongoDB:", e);
            return;
        }

        String coll = plugin
                .getConfig().getDatabase_mongodb_collection();

        String database = plugin
                .getConfig().getDatabase_mongodb_database();

        collection = client.getDatabase(database).getCollection(coll);
        if (isConnected()) log.info("Successfully connected to MongoDB!");
        else log.error("Failed to connect to MongoDB!");
    }

    @Override
    public void disconnect() {
        client.close();
    }

    @Override
    public boolean isConnected() {
        return client != null && client.getClusterDescription().isCompatibleWithDriver();
    }

    @Override
    public boolean saveUser(CoinUser user) {
        boolean exists = hasUser(user.getUniqueId()), wasSuccessful = false;
        if (!exists) {
            wasSuccessful = collection.insertOne(BsonUtils.toBson(user)).wasAcknowledged();
        }

        CoinUser oldUser = getUser(user.getUniqueId()).orElse(null);
        boolean shouldOverride = oldUser.getLastSaved() < user.getLastSaved();
        if(shouldOverride) {
            user.setCoins(oldUser.getCoins());
            user.setLastKnownName(oldUser.getLastKnownName());
            user.setMultiplier(oldUser.getMultiplier());
            user.setLastSaved(oldUser.getLastSaved());

            wasSuccessful = collection.replaceOne(
                    BsonUtils.filterForUUID(user.getUniqueId()),
                    BsonUtils.toBson(user))
                    .wasAcknowledged();
        } else user.setLastSaved(System.currentTimeMillis());

        return wasSuccessful;
    }

    @Override
    public boolean deleteUser(CoinUser user) {
        if (cache.get(user.getUniqueId()) != null) cache.remove(user.getUniqueId());
        return collection.deleteOne(BsonUtils.filterForUUID(user.getUniqueId())).wasAcknowledged();
    }

    @Override
    public Optional<CoinUser> getUser(UUID uuid) {
        // If the user is cached, return it.
        if (cache.containsKey(uuid)) return Optional.of(cache.get(uuid));

        // If the user is not cached, check the database.
        Document document = collection.find(BsonUtils.filterForUUID(uuid)).first();
        if (document == null) return Optional.empty();

        CoinUser user = BsonUtils.fromBson(document.toBsonDocument());
        user.setLastSaved(System.currentTimeMillis());

        return Optional.of(user);
    }

    @Override
    public Optional<CoinUser> getUser(String name) {
        for (CoinUser user : cache.values()) {
            if (user.getLastKnownName().equalsIgnoreCase(name)) return Optional.of(user);
        }

        Document document = collection.find(BsonUtils.filterForName(name)).first();
        if (document == null) return Optional.empty();
        return Optional.of(BsonUtils.fromBson(document.toBsonDocument()));
    }

    @Override
    public boolean hasUser(UUID uuid) {
        return collection.find(BsonUtils.filterForUUID(uuid)).first() != null;
    }

    @Override
    public CompletableFuture<Boolean> saveUserAsync(CoinUser user) {
        return CompletableFuture.supplyAsync(() -> saveUser(user), executor);
    }

    @Override
    public CompletableFuture<Boolean> deleteUserAsync(CoinUser user) {
        return CompletableFuture.supplyAsync(() -> deleteUser(user), executor);
    }

    @Override
    public CompletableFuture<Optional<CoinUser>> getUserAsync(UUID uuid) {
        //return if not cached async
        return CompletableFuture.supplyAsync(() -> getUser(uuid), executor);
    }

    @Override
    public CompletableFuture<Optional<CoinUser>> getUserAsync(String name) {
        return CompletableFuture.supplyAsync(() -> getUser(name), executor);
    }

    @Override
    public void invalidate(UUID uuid) {
        // Remove the user from the cache. And save him
        CoinUser user = cache.remove(uuid);
        if (user != null) saveUser(user);
    }

    @Override
    public boolean loadAll(List<UUID> uuids) {
        AtomicInteger successCount = new AtomicInteger(0);

        for (UUID uuid : uuids) {
            Optional<CoinUser> user = getUser(uuid);
            if (user.isPresent()) {
                cache.put(uuid, user.get());
                successCount.getAndIncrement();
            }
        }

        return successCount.get() == uuids.size();
    }

    @Override
    public void invalidateAll() {
        // Save all users in the cache
        cache.values().forEach(this::saveUser);
        // Clear the cache
        cache.clear();
    }
}
