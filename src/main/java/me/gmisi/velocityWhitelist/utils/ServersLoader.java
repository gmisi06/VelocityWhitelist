package me.gmisi.velocityWhitelist.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class ServersLoader {

    public static void loadServers(ProxyServer proxy, ConfigManager configManager, Logger logger) {

        ensureServerConfig(configManager.getConfig(), "VelocityProxy");

        proxy.getAllServers().forEach(server ->
                ensureServerConfig(configManager.getConfig(), server.getServerInfo().getName())
        );

        try {
            configManager.getConfig().update();
            configManager.getConfig().save();
        } catch (IOException e) {
            logger.error("Could not create servers list.");
        }

        logger.info("Loaded servers list.");

    }

    private static void ensureServerConfig(YamlDocument config, String serverName) {
        String basePath = "servers." + serverName;

        if (!config.contains(basePath + ".enabled")) {
            config.set(basePath + ".enabled", false);
        }

        if (!config.contains(basePath + ".whitelisted")) {
            config.set(basePath + ".whitelisted", new ArrayList<String>());
        }
    }
}
