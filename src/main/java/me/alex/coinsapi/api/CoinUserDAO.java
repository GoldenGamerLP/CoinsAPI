package me.alex.coinsapi.api;

import org.jetbrains.annotations.Blocking;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CoinUserDAO {

    /**
     * Connects to the database
     */
    @Blocking
    void connect();

    /**
     * Disconnects from the database
     */
    @Blocking
    void disconnect();

    /**
     * Saves a user to the database
     *
     * @param user the user to save
     * @return true if the user was saved successfully
     */
    @Blocking
    boolean saveUser(CoinUser user);

    /**
     * Deletes a user from the database
     *
     * @param user the user to delete
     * @return true if the user was deleted successfully
     */
    @Blocking
    boolean deleteUser(CoinUser user);

    /**
     * Gets a user from the database
     *
     * @param uuid the uuid of the user
     * @return the user if found
     */
    @Blocking
    Optional<CoinUser> getUser(UUID uuid);

    /**
     * Gets a user from the database
     *
     * @param name the name of the user
     * @return the user if found
     */
    @Blocking
    Optional<CoinUser> getUser(String name);

    /**
     * Checks if the user exists in the database
     *
     * @param uuid the uuid of the user
     * @return true if the user exists
     */
    @Blocking
    boolean hasUser(UUID uuid);

    /**
     * Saves a user asynchronously
     *
     * @param user the user to save
     * @return true if the user was saved successfully
     */
    CompletableFuture<Boolean> saveUserAsync(CoinUser user);

    /**
     * Deletes a user asynchronously
     *
     * @param user the user to delete
     * @return true if the user was deleted successfully
     */
    CompletableFuture<Boolean> deleteUserAsync(CoinUser user);

    /**
     * Gets a user asynchronously
     *
     * @param uuid the uuid of the user
     * @return the user if found
     */
    CompletableFuture<Optional<CoinUser>> getUserAsync(UUID uuid);

    /**
     * Gets a user asynchronously
     *
     * @param name the name of the user
     * @return the user if found
     */
    CompletableFuture<Optional<CoinUser>> getUserAsync(String name);
}
