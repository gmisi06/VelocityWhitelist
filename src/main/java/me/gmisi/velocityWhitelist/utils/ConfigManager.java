package me.gmisi.velocityWhitelist.utils;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import me.gmisi.velocityWhitelist.lang.LangKey;
import me.gmisi.velocityWhitelist.lang.LanguageFileReader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Getter
public class ConfigManager {

    private static ConfigManager instance;

    private final YamlDocument config;
    private final Path dataDirectory;
    private final Logger logger;
    private WhitelistManager whitelistManager;
    private LanguageFileReader lang;


    public ConfigManager(final Path dataDirectory, final Logger logger, final ProxyServer server) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;

        try {
            config = YamlDocument.create(
                    new File(dataDirectory.toFile(), "config.yml"),
                    Objects.requireNonNull(getClass().getResourceAsStream("/config.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder()
                            .setVersioning(new BasicVersioning("file-version"))
                            .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS)
                            .build()
            );
            config.update();
            config.save();

            whitelistManager = new WhitelistManager(config, logger);
            lang = new LanguageFileReader(config.getString("lang", "en"), dataDirectory, logger);

        } catch (IOException e) {
            shutdownPlugin(logger, server, "Could not create/load plugin config! This plugin will now shutdown.");
            throw new RuntimeException("Failed to load config.yml", e);
        }
    }


    public static void init(Path dataDirectory, Logger logger, ProxyServer server) {
        if (instance == null) {
            instance = new ConfigManager(dataDirectory, logger, server);
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConfigManager is not initialized yet!");
        }
        return instance;
    }

    public String get(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    private void shutdownPlugin(Logger logger, ProxyServer server, String reason) {
        logger.error(reason);
        Optional<PluginContainer> container = server.getPluginManager().getPlugin("VelocityWhitelist");
        container.ifPresent(pluginContainer -> pluginContainer.getExecutorService().shutdown());
    }

    public String getFormatted(String key, String defaultValue, Map<String, String> values) {
        String template = get(key, defaultValue);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    public void reload(CommandSource source) {
        try {
            config.reload();
            config.update();
            config.save();
            whitelistManager = new WhitelistManager(config, logger);
            lang = new LanguageFileReader(config.getString("lang", "en"), dataDirectory, logger);
        } catch (IOException e) {
            MessageUtil.sendPrefixed(source, getLang().get(LangKey.RELOAD_ERROR));
        }
    }
}
