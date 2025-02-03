package me.gmisi.velocityWhitelist.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class ServersLoader {

    public static void loadServers(ProxyServer proxy, Logger logger) {
        YamlDocument config = VelocityWhitelist.getConfig();

        if (!config.contains("servers.VelocityProxy")) {
            config.set("servers.VelocityProxy.enabled", false);
            config.set("servers.VelocityProxy.whitelisted", new ArrayList<String>());
        }

        proxy.getAllServers().forEach(server -> {
            String serverName = server.getServerInfo().getName();

            if (!config.contains("servers." + serverName)) {
                config.set("servers."+ serverName  +".enabled", false);
                config.set("servers." + serverName + ".whitelisted", new ArrayList<String>());
            }
        });

        try {
            config.update();
            config.save();
        } catch (IOException e) {
            logger.error("Could not create servers list.");
        }

        logger.info("Loaded servers list.");

    }
}
