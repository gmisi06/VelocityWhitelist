package me.gmisi.velocityWhitelist.listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import me.gmisi.velocityWhitelist.utils.ConfigManager;
import me.gmisi.velocityWhitelist.utils.MessageUtil;
import me.gmisi.velocityWhitelist.utils.PermissionUtil;
import me.gmisi.velocityWhitelist.utils.WhitelistManager;

import java.util.List;

public class LoginListener {

    private final ConfigManager configManager;

    public LoginListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        if (configManager.getWhitelistManager().isWhitelistEnabled("VelocityProxy")) {
            List<String> whitelisted = configManager.getWhitelistManager().getWhitelistedPlayers("VelocityProxy");
            Player player = event.getPlayer();

            if (!whitelisted.contains(player.getUsername()) && !(PermissionUtil.hasGlobal(player, "bypass"))) {
                event.setResult(ResultedEvent.ComponentResult.denied(MessageUtil.getPrefixed(configManager.get("proxy-kick-message", "You are not on the whitelist."))));
            }
        }
    }
}
