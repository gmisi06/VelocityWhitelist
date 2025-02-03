package me.gmisi.velocityWhitelist.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class LanguageManager {
    private final Path dataDirectory;
    private YamlDocument langConfig;

    public LanguageManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @SuppressWarnings("unused") // For more language support.
    public void loadLanguageFile(String type) throws IOException {
        File langFile = new File(dataDirectory.toFile(), "lang/lang_" + type + ".yml");
        langConfig = YamlDocument.create(
                langFile,
                Objects.requireNonNull(getClass().getResourceAsStream("/lang_" + type + ".yml")),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder()
                        .setAutoUpdate(false)
                        .build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().build()
        );
        langConfig.save();
    }

    public void loadLanguageFile() throws IOException {
        File langFile = new File(dataDirectory.toFile(), "lang/lang_en.yml");
        langConfig = YamlDocument.create(
                langFile,
                Objects.requireNonNull(getClass().getResourceAsStream("/lang/lang_en.yml")),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder()
                        .setAutoUpdate(false)
                        .build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().build()
        );
        langConfig.save();
    }

    public YamlDocument getLanguageConfig() {
        return langConfig;
    }
}
