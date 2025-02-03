package me.gmisi.velocityWhitelist.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.CommandHandler;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ServerPreConnectionListener {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final YamlDocument config;
    private final Logger logger;

    public ServerPreConnectionListener(YamlDocument config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    @Subscribe
    public void onServerPreConnectionEvent(ServerPreConnectEvent event) {
        logger.info(event.getOriginalServer().getServerInfo().getName());

        String serverName = event.getOriginalServer().getServerInfo().getName();

        if ((boolean) config.get("servers." + serverName + ".enabled")) {
            List<String> whitelisted = config.getStringList("servers." + serverName  + ".whitelisted", new ArrayList<>());
            Player player = event.getPlayer();

            if (!whitelisted.contains(player.getUsername()) && !(player.hasPermission(CommandHandler.PERMISSION_ROOT + ".bypass"))) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                String message = config.getString("kick-message")
                        .replace("{server}", serverName
                );
                player.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX + " " + message )));
            }

        }
    }
}
