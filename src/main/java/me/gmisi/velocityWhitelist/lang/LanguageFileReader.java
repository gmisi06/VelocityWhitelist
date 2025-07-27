package me.gmisi.velocityWhitelist.lang;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class LanguageFileReader {

    private final String langCode;
    private final Logger logger;
    private YamlDocument languageFile;

    public LanguageFileReader(String langCode, Path dataDirectory, Logger logger) {
        this.langCode = langCode.toLowerCase();
        this.logger = logger;
        copyDefaultLangFiles(dataDirectory);
        loadLanguageFile(dataDirectory);
    }

    private void loadLanguageFile(Path dataDirectory) {
        File file = new File(dataDirectory.toFile(), "lang/lang_" + langCode + ".yml");
        InputStream resource = getClass().getResourceAsStream("/lang/lang_" + langCode + ".yml");

        if (resource == null) {
            logErrorAndShutdown("The file '/resources/lang/lang_" + langCode + ".yml' was not found in the plugin JAR!");
            throw new RuntimeException("Missing language resource file: lang_" + langCode + ".yml");
        }

        try {
            this.languageFile = YamlDocument.create(
                    file,
                    Objects.requireNonNull(getClass().getResourceAsStream("/lang/lang_" + langCode + ".yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder()
                            .setVersioning(new BasicVersioning("file-version"))
                            .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS)
                            .build()
            );
            languageFile.update();
            languageFile.save();

        } catch (IOException e) {
            logErrorAndShutdown("Failed to load lang_" + langCode + ".yml: " + e.getMessage());
            throw new RuntimeException("Could not load language file", e);
        }
    }

    public String get(LangKey.LangEntry key) {
        return languageFile.getString(key.path, key.defaultValue);
    }

    public String getFormatted(LangKey.LangEntry key, Map<String, String> values) {
        String template = get(key);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    private void logErrorAndShutdown(String message) {
        logger.error(message);
    }

    private void copyDefaultLangFiles(Path dataDirectory) {
        String[] defaultLangs = {"en", "hu", "ua"};

        File langDir = new File(dataDirectory.toFile(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        for (String langCode : defaultLangs) {
            String fileName = "lang_" + langCode + ".yml";
            File targetFile = new File(langDir, fileName);

            if (!targetFile.exists()) {
                try (InputStream in = getClass().getResourceAsStream("/lang/" + fileName)) {
                    if (in == null) {
                        logger.warn("Default language file not found in JAR: " + fileName);
                        continue;
                    }

                    java.nio.file.Files.copy(in, targetFile.toPath());
                    logger.info("Copied default language file: " + fileName);
                } catch (IOException e) {
                    logger.error("Failed to copy language file " + fileName, e);
                }
            }
        }
    }

}

