package me.gmisi.velocityWhitelist.utils;

import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.IOException;
import java.io.InputStream;

public class LanguageManager {
    private YamlDocument langConfig;

    public LanguageManager() {
    }

    public void loadLanguageFile(String type) throws IOException {

        InputStream defaultLangFile = getClass().getResourceAsStream("/lang/lang_" + type + ".yml");

        if (defaultLangFile == null) {
            loadLanguageFile();
            return;
        }

        langConfig = YamlDocument.create(defaultLangFile);
    }

    public void loadLanguageFile() throws IOException {
        InputStream defaultLangFile = getClass().getResourceAsStream("/lang/lang_en.yml");

        if (defaultLangFile == null) {
            throw new IOException("Failed to load language file!");
        }

        langConfig = YamlDocument.create(defaultLangFile);
    }

    public YamlDocument getLanguageConfig() {
        return langConfig;
    }
}
