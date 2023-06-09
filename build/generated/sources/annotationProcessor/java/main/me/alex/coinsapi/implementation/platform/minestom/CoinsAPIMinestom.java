package me.alex.coinsapi.implementation.platform.minestom;

import dev.hypera.chameleon.ChameleonPluginData;
import dev.hypera.chameleon.exception.ChameleonRuntimeException;
import dev.hypera.chameleon.exception.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platform.minestom.MinestomChameleon;
import java.lang.Override;
import java.util.Arrays;
import me.alex.coinsapi.implementation.CoinsAPI;
import net.minestom.server.extensions.Extension;

public class CoinsAPIMinestom extends Extension {
    private MinestomChameleon chameleon;

    public CoinsAPIMinestom() {
        try {
            ChameleonPluginData pluginData = ChameleonPluginData.builder("CoinsAPI", "@version@").description("CoinsAPI").url("").authors(Arrays.asList("Alex")).build();
            this.chameleon = MinestomChameleon.create(CoinsAPI.class, this, pluginData).load();
        } catch (ChameleonInstantiationException ex) {
            getLogger().error("An error occurred while loading Chameleon", ex);
            throw new ChameleonRuntimeException(ex);
        }
    }

    @Override
    public void initialize() {
        this.chameleon.onEnable();
    }

    @Override
    public void terminate() {
        this.chameleon.onDisable();
    }
}
