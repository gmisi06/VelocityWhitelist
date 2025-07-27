package me.gmisi.velocityWhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.gmisi.velocityWhitelist.commands.CommandHandler;
import me.gmisi.velocityWhitelist.listeners.LoginListener;
import me.gmisi.velocityWhitelist.listeners.ServerPreConnectionListener;
import me.gmisi.velocityWhitelist.utils.ConfigManager;
import me.gmisi.velocityWhitelist.utils.ServersLoader;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "VelocityWhitelist",
        name = "VelocityWhitelist",
        version = "1.1.0-SNAPSHOT",
        description = "A Velocity Proxy server whitelist plugin.",
        authors = {"gmisi"}
)
public class VelocityWhitelist {

    @Getter
    private final ProxyServer proxy;

    @Getter
    private final Logger logger;

    private final ConfigManager configManager;

    @Getter
    private final Path dataDirectory;

    @Inject
    public VelocityWhitelist(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        ConfigManager.init(dataDirectory, logger, server);
        configManager = ConfigManager.getInstance();

        ServersLoader.loadServers(proxy, configManager, logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = proxy.getCommandManager();

        CommandMeta commandMeta = commandManager.metaBuilder("velocitywhitelist")
                .aliases("vwl")
                .plugin(this)
                .build();

        BrigadierCommand reloadCommandToRegister = CommandHandler.createBrigadierCommand(proxy, "velocitywhitelist", configManager);
        commandManager.register(commandMeta, reloadCommandToRegister);

        proxy.getEventManager().register(this, new ServerPreConnectionListener(configManager));
        proxy.getEventManager().register(this, new LoginListener(configManager));

        logger.info("VelocityWhitelist plugin has initialized!");
    }

    public void shutdown(String reason) {
        logger.error(reason);
        this.shutdown();
    }

    public void shutdown() {
        Optional<PluginContainer> container = proxy.getPluginManager().getPlugin("VelocityWhitelist");
        container.ifPresent(plugin -> plugin.getExecutorService().shutdown());
    }
}
