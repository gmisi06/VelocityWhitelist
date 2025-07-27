package me.gmisi.velocityWhitelist.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import me.gmisi.velocityWhitelist.utils.ConfigManager;
import me.gmisi.velocityWhitelist.utils.MessageUtil;
import me.gmisi.velocityWhitelist.utils.PermissionUtil;

import java.util.List;
import java.util.Map;

public class ServerPreConnectionListener {

    private final ConfigManager configManager;

    public ServerPreConnectionListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Subscribe
    public void onServerPreConnectionEvent(ServerPreConnectEvent event) {
        String serverName = event.getOriginalServer().getServerInfo().getName();

        if (configManager.getWhitelistManager().isWhitelistEnabled(serverName)) {
            List<String> whitelisted = configManager.getWhitelistManager().getWhitelistedPlayers(serverName);

            Player player = event.getPlayer();

            if (!whitelisted.contains(player.getUsername()) && !PermissionUtil.hasGlobal(player, "bypass")) {
                Map<String, String> placeholders = Map.of(
                        "server", serverName
                );

                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                MessageUtil.sendPrefixed(player, configManager.getFormatted("kick-message", "&cYou are not whitelisted for {server}", placeholders));
            }
        }
    }
}
