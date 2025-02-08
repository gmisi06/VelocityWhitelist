package me.gmisi.velocityWhitelist.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class LanguageManager {
    private final Path dataDirectory;
    private YamlDocument langConfig;

    public LanguageManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        copyLanguageFiles();
    }

    @SuppressWarnings("unused") // For more language support.
    public void loadLanguageFile(String type) throws IOException {
        File langFile = new File(dataDirectory.toFile(), "lang/lang_" + type + ".yml");

        langConfig = YamlDocument.create(
                langFile,
                Objects.requireNonNull(getClass().getResourceAsStream("/lang/lang_" + type + ".yml")),
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

    public void copyLanguageFiles() {
        File langDir = new File(dataDirectory.toFile(), "lang");
        if (!langDir.exists() && !langDir.mkdirs()) {
            throw new RuntimeException("Failed to create lang directory at " + langDir.getAbsolutePath());
        }

        String[] languages = {"en", "hu", "ua"};

        for (String lang : languages) {
            File targetFile = new File(langDir, "lang_" + lang + ".yml");
            if (!targetFile.exists()) {
                try (InputStream in = getClass().getResourceAsStream("/lang/lang_" + lang + ".yml")) {
                    if (in != null) {
                        Files.copy(in, targetFile.toPath());
                    } else {
                        System.err.println("Language file for '" + lang + "' not found in jar!");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to copy language file for '" + lang + "'", ex);
                }
            }
        }
    }
}
