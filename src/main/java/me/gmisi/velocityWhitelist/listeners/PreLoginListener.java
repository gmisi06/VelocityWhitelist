package me.gmisi.velocityWhitelist.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class PreLoginListener {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final YamlDocument config;

    public PreLoginListener(YamlDocument config) {
        this.config = config;
    }

    @Subscribe
    public void onPreLoginEvent(PreLoginEvent event) {
        if ((boolean) config.get("servers.VelocityProxy.enabled")) {
            List<?> whitelisted = config.getStringList("servers.VelocityProxy.whitelisted", new ArrayList<>());
            String playerName = event.getUsername();

            if (!whitelisted.contains(playerName)) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(serializer.deserialize( VelocityWhitelist.PREFIX + " &cYou are not on the whitelist.")));
            }
        }
    }
}
