package me.alex.coinsapi.api;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The main API class for the CoinsAPI. See example usage in the test folder.
 */
public interface CoinAPI {

    /**
     * Gets the Cache of the CoinAPI
     *
     * @return CoinsUserCache
     */
    CoinsUserCache getCache();

    /**
     * Directly gets the CoinUserDAO Instance
     *
     * @return CoinUserDAO
     * @see CoinUserDAO
     */
    CoinUserDAO getDatabase();

    /**
     * Gets the CoinUser from the CoinUserDAO. If none is found, it will return null.
     * This method is not recommended to use, as it is blocking. Use the async method instead.
     *
     * @param uuid The UUID of the player
     * @return CoinUser
     * @see CoinUserDAO#getUserAsync(UUID)
     */
    @Nullable
    @Blocking
    default Optional<CoinUser> getUser(UUID uuid) {
        return getDatabase().getUser(uuid);
    }

    /**
     * Gets the CoinUser from the CoinUserDAO. If none is found, it will return null.
     * This method is not recommended to use, as it is blocking. Use the async method instead.
     *
     * @param name The name of the player
     * @return CoinUser
     * @see CoinUserDAO#getUserAsync(String)
     */
    @Nullable
    @Blocking
    default CoinUser getUser(String name) {
        return getDatabase().getUser(name).orElse(null);
    }

    /**
     * Checks if the user exists in the database.
     *
     * @param uuid The UUID of the player
     * @return boolean
     */
    @Blocking
    default boolean hasUser(UUID uuid) {
        return getDatabase().hasUser(uuid);
    }

    /**
     * Saves the user synchronously.
     *
     * @param user The user to save
     */
    @Blocking
    default void saveUser(CoinUser user) {
        getDatabase().saveUser(user);
    }

    /**
     * Deletes the user synchronously.
     *
     * @param user The user to delete
     */
    @Blocking
    default void deleteUser(CoinUser user) {
        getDatabase().deleteUser(user);
    }

    /**
     * Edits the user asynchronously. The User will be saved after the consumer is done.
     *
     * @param consumer The consumer to edit the user
     * @param uuid     The UUID of the player
     */
    default void editUser(UUID uuid, Consumer<CoinUser> consumer) {
        getDatabase().getUserAsync(uuid).thenAccept(user -> {
            if (user.isPresent()) {
                consumer.accept(user.get());
                getDatabase().saveUser(user.get());
            }
        });
    }

}
