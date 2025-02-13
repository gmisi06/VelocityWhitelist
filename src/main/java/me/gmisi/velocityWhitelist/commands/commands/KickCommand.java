package me.gmisi.velocityWhitelist.commands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.CommandHandler;
import me.gmisi.velocityWhitelist.commands.VelocitySubCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class KickCommand implements VelocitySubCommand {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final ProxyServer proxy;
    private final YamlDocument config;

    public KickCommand(ProxyServer proxy, YamlDocument config) {
        this.proxy = proxy;
        this.config = config;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {

        return LiteralArgumentBuilder.<CommandSource>literal("kick")
                .executes(context -> {
                    CommandSource source = context.getSource();

                    source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX)));
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-kick"))));

                    return Command.SINGLE_SUCCESS;
                })
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("server", word())
                        .suggests((context, builder) -> {

                            if ("velocityproxy".startsWith(builder.getRemaining().toLowerCase())) {
                                builder.suggest("VelocityProxy");
                            }

                            proxy.getAllServers().stream()
                                    .map(server -> server.getServerInfo().getName())
                                    .filter(name -> name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                                    .limit(10)
                                    .forEach(builder::suggest);

                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            CommandSource source = context.getSource();

                            String serverName = context.getArgument("server", String.class);

                            if (!config.contains("servers." + serverName)) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("server-not-exists")
                                        .replace("{server}", serverName)));
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!source.hasPermission(CommandHandler.PERMISSION_ROOT + ".kick.*") && !source.hasPermission(CommandHandler.PERMISSION_ROOT + ".kick." + serverName)) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("kick-no-perm")
                                        .replace("{server}", serverName)));
                                return Command.SINGLE_SUCCESS;
                            }

                            try {
                                List<String> whitelisted = config.getStringList("servers." + serverName + ".whitelisted", new ArrayList<>());

                                Optional<RegisteredServer> optionalServer = proxy.getServer(serverName);

                                if (optionalServer.isPresent()) {
                                    RegisteredServer server = optionalServer.get();
                                    Collection<Player> players = server.getPlayersConnected();

                                    players.forEach(player -> {
                                        if (!whitelisted.contains(player.getUsername())) {
                                            String message = config.getString("kick-message")
                                                    .replace("{server}", serverName
                                                    );
                                            player.disconnect((serializer.deserialize(VelocityWhitelist.PREFIX + " " + message )));
                                        }
                                    });
                                }

                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("kick-success")));
                            } catch (Exception e) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("load-error")));
                            }

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
