package me.alex.coinsapi.implementation;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.ChameleonPluginData;
import dev.hypera.chameleon.exception.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platform.minestom.MinestomChameleon;
import net.minestom.server.extensions.Extension;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class MinestomBootstrap {

    public static void main(String[] args) throws ChameleonInstantiationException {
        System.setProperty("minestom.use-new-chunk-sending", "true");

        MinestomChameleon.create(CoinsAPI.class, new Extension() {
            @Override
            public void initialize() {

            }

            @Override
            public void terminate() {

            }
        }, new ChameleonPluginData() {
            @Override
            public @NotNull String getName() {
                return "CoinsAPI";
            }

            @Override
            public @NotNull String getVersion() {
                return "1.0.0";
            }

            @Override
            public @NotNull Optional<String> getDescription() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<String> getUrl() {
                return Optional.empty();
            }

            @Override
            public @NotNull Collection<String> getAuthors() {
                return List.of("Alex");
            }
        }).load();
    }
}
