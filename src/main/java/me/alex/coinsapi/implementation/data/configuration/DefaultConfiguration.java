package me.alex.coinsapi.implementation.data.configuration;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.hypera.chameleon.Chameleon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.alex.coinsapi.implementation.CoinsAPI;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class DefaultConfiguration {

    private final Path path;
    @Getter
    private YamlDocument configuration;

    public DefaultConfiguration(CoinsAPI plugin) {
        Chameleon chameleon = plugin.getChameleon();
        this.path = chameleon.getDataFolder();
    }

    public void load() {
        try {
            this.configuration = YamlDocument.create(
                    path.resolve("config.yml").toFile()
                    , DefaultConfiguration.class.getResourceAsStream("/config.yml"),
                    GeneralSettings.DEFAULT,
                    getDefaultLoaderSettings(),
                    DumperSettings.DEFAULT,
                    getDefaultUpdaterSettings()
            );
        } catch (IOException e) {
            log.error("Failed to load configuration file", e);
        }
    }

    public void save() {
        try {
            configuration.save();
        } catch (IOException e) {
            log.error("Failed to save configuration file", e);
        }
    }

    private UpdaterSettings getDefaultUpdaterSettings() {
        return UpdaterSettings.builder()
                .setAutoSave(true)
                .setVersioning(new BasicVersioning("config-version"))
                .build();
    }

    private LoaderSettings getDefaultLoaderSettings() {
        return LoaderSettings.builder()
                .setAutoUpdate(true)
                .setDetailedErrors(true)
                .setCreateFileIfAbsent(true)
                .build();
    }
}
