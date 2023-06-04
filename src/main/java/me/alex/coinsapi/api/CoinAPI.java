package me.alex.coinsapi.api;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public interface CoinAPI {

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
    default CoinUser getUser(UUID uuid) {
        return getDatabase().getUser(uuid).orElse(null);
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
    default void editUser(Consumer<CoinUser> consumer, UUID uuid) {
        getDatabase().getUserAsync(uuid).thenAccept(user -> {
            if (user.isPresent()) {
                consumer.accept(user.get());
                getDatabase().saveUser(user.get());
            }
        });
    }

}
