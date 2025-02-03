package me.gmisi.velocityWhitelist.listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.CommandHandler;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class LoginListener {
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final YamlDocument config;

    public LoginListener(YamlDocument config) {
        this.config = config;
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        if ((boolean) config.get("servers.VelocityProxy.enabled")) {
            List<?> whitelisted = config.getStringList("servers.VelocityProxy.whitelisted", new ArrayList<>());
            Player player = event.getPlayer();

            if (!whitelisted.contains(player.getUsername()) && !(player.hasPermission(CommandHandler.PERMISSION_ROOT + ".bypass"))) {
                event.setResult(ResultedEvent.ComponentResult.denied(serializer.deserialize( VelocityWhitelist.PREFIX + " " + config.getString("proxy-kick-message"))));
            }
        }
    }
}
