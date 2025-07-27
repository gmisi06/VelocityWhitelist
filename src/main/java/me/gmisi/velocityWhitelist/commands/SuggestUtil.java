package me.gmisi.velocityWhitelist.commands;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class for providing Brigadier command suggestion completions.
 */
public class SuggestUtil {


    /**
     * Suggests server names matching the given input, including a static "VelocityProxy" option.
     *
     * @param proxy   The Velocity proxy instance to retrieve server names from.
     * @param builder The SuggestionsBuilder provided by Brigadier.
     * @return A future containing the built suggestions.
     */
    public static CompletableFuture<Suggestions> playerSuggests(ProxyServer proxy, SuggestionsBuilder builder) {
        proxy.getAllPlayers().stream()
                .map(Player::getUsername)
                .filter(name -> name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                .limit(10)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    /**
     * Suggests currently online player usernames matching the given input.
     *
     * @param proxy   The Velocity proxy instance to retrieve players from.
     * @param builder The SuggestionsBuilder provided by Brigadier.
     * @return A future containing the built suggestions.
     */
    public static CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> serverSuggests(ProxyServer proxy, SuggestionsBuilder builder) {
        if ("velocityproxy".startsWith(builder.getRemaining().toLowerCase())) {
            builder.suggest("VelocityProxy");
        }

        proxy.getAllServers().stream()
                .map(server -> server.getServerInfo().getName())
                .filter(name -> name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                .limit(10)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}
