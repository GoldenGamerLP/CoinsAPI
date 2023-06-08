package me.alex.coinsapi.implementation.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import me.alex.coinsapi.api.CoinUser;
import me.alex.coinsapi.api.CoinUserDAO;
import me.alex.coinsapi.api.CoinsUserCache;
import me.alex.coinsapi.implementation.CoinsAPI;
import me.alex.coinsapi.implementation.utils.BsonUtils;
import org.bson.Document;
import org.bson.UuidRepresentation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MongoDBImpl implements CoinUserDAO, CoinsUserCache {

    private final CoinsAPI plugin;
    private final MongoClientSettings settings;
    private final Map<UUID, CoinUser> cache;
    private MongoClient client;
    private MongoCollection<Document> collection;


    public MongoDBImpl(CoinsAPI plugin) {
        this.plugin = plugin;

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        String uri = plugin
                .getConfiguration()
                .getConfiguration()
                .getString("mongodb_uri");

        settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        cache = new ConcurrentHashMap<>(8, 0.9f, 2);
    }

    @Override
    public void connect() {
        client = MongoClients.create(settings);

        String coll = plugin
                .getConfiguration()
                .getConfiguration()
                .getString("mongodb_collection");

        String database = plugin
                .getConfiguration()
                .getConfiguration()
                .getString("mongodb_database");

        collection = client.getDatabase(database).getCollection(coll);
    }

    @Override
    public void disconnect() {
        client.close();
    }

    @Override
    public boolean saveUser(CoinUser user) {
        boolean exists = hasUser(user.getUniqueId()), wasSuccessful;
        if (!exists) {
            wasSuccessful = collection.insertOne(BsonUtils.toBson(user)).getInsertedId() != null;
        } else wasSuccessful = collection.replaceOne(
                BsonUtils.filterForUUID(user.getUniqueId()),
                BsonUtils.toBson(user)).getModifiedCount() == 1;
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
        return CompletableFuture.supplyAsync(() -> saveUser(user));
    }

    @Override
    public CompletableFuture<Boolean> deleteUserAsync(CoinUser user) {
        return CompletableFuture.supplyAsync(() -> deleteUser(user));
    }

    @Override
    public CompletableFuture<Optional<CoinUser>> getUserAsync(UUID uuid) {
        //return if not cached async
        return CompletableFuture.supplyAsync(() -> getUser(uuid));
    }

    @Override
    public CompletableFuture<Optional<CoinUser>> getUserAsync(String name) {
        return CompletableFuture.supplyAsync(() -> getUser(name));
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
