package me.gmisi.velocityWhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;
import me.gmisi.velocityWhitelist.commands.CommandHandler;
import me.gmisi.velocityWhitelist.listeners.LoginListener;
import me.gmisi.velocityWhitelist.listeners.ServerPreConnectionListener;
import me.gmisi.velocityWhitelist.utils.ConfigManager;
import me.gmisi.velocityWhitelist.utils.LanguageManager;
import me.gmisi.velocityWhitelist.utils.ServersLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "VelocityWhitelist",
        name = "VelocityWhitelist",
        version = "1.0.1-SNAPSHOT",
        description = "A Velocity Proxy server whitelist plugin.",
        authors = {"gmisi"}
)
public class VelocityWhitelist {

    public final static String PREFIX = "&9&l[VelocityWhitelist]";

    private final ProxyServer proxy;

    @Getter
    private final Logger logger;

    @Getter
    private static YamlDocument config;

    @Setter
    @Getter
    private static YamlDocument lang;

    @Getter
    private final Path dataDirectory;

    @Inject
    public VelocityWhitelist(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        ConfigManager configManager = new ConfigManager(dataDirectory, logger, server);

        config = configManager.getConfig();

        try {
            LanguageManager languageManager = new LanguageManager(dataDirectory);
            if (!config.contains("lang")) {
                languageManager.loadLanguageFile();
            }
            else {
                String language = config.getString("lang");
                languageManager.loadLanguageFile(language);
            }

            lang = languageManager.getLanguageConfig();
        } catch (IOException e) {
            logger.error("Failed to load language file!", e);
        }

        ServersLoader.loadServers(proxy, logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = proxy.getCommandManager();

        CommandMeta commandMeta = commandManager.metaBuilder("velocitywhitelist")
                .aliases("vwl")
                .plugin(this)
                .build();

        BrigadierCommand reloadCommandToRegister = CommandHandler.createBrigadierCommand(proxy, "velocitywhitelist", dataDirectory);
        commandManager.register(commandMeta, reloadCommandToRegister);

        proxy.getEventManager().register(this, new ServerPreConnectionListener(config, logger));
        proxy.getEventManager().register(this, new LoginListener(config));

        logger.info("VelocityWhitelist plugin has initialized!");
    }
}
