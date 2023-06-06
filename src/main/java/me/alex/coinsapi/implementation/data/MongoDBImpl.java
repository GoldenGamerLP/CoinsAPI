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
import me.alex.coinsapi.implementation.CoinsAPI;
import me.alex.coinsapi.implementation.utils.BsonUtils;
import org.bson.Document;
import org.bson.UuidRepresentation;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDBImpl implements CoinUserDAO {

    private final CoinsAPI plugin;
    private final MongoClientSettings settings;
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

        System.out.println("" + wasSuccessful + " " + user.getUniqueId() + exists);
        return wasSuccessful;
    }

    @Override
    public boolean deleteUser(CoinUser user) {
        return collection.deleteOne(BsonUtils.filterForUUID(user.getUniqueId())).wasAcknowledged();
    }

    @Override
    public Optional<CoinUser> getUser(UUID uuid) {
        Document document = collection.find(BsonUtils.filterForUUID(uuid)).first();
        if (document == null) return Optional.empty();
        return Optional.of(BsonUtils.fromBson(document.toBsonDocument()));
    }

    @Override
    public Optional<CoinUser> getUser(String name) {
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
        return CompletableFuture.supplyAsync(() -> getUser(uuid));
    }

    @Override
    public CompletableFuture<Optional<CoinUser>> getUserAsync(String name) {
        return CompletableFuture.supplyAsync(() -> getUser(name));
    }

}
