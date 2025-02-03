package me.gmisi.velocityWhitelist.commands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.VelocitySubCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class AddCommand implements VelocitySubCommand {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final ProxyServer proxy;
    private final YamlDocument config;

    public AddCommand(ProxyServer proxy, YamlDocument config) {
        this.proxy = proxy;
        this.config = config;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {
        return LiteralArgumentBuilder.<CommandSource>literal("add")
                .executes(context -> {
                    CommandSource source = context.getSource();

                    source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX)));
                    source.sendMessage((serializer.deserialize("&7/vwl add <player> <server> - Add player to the specified server's whitelist.")));

                    return Command.SINGLE_SUCCESS;
                })
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("player", word())
                        .suggests((context, builder) -> {
                            proxy.getAllPlayers().stream()
                                    .map(Player::getUsername)
                                    .filter(name -> name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                                    .limit(10)
                                    .forEach(builder::suggest);

                            return builder.buildFuture();
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

                                    String playerName = context.getArgument("player", String.class);
                                    String serverName = context.getArgument("server", String.class);

                                    if (!config.contains("servers." + serverName)) {
                                        source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cThe server '" + serverName + "' does not exist."));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (!source.hasPermission("velocity.add.*") && !source.hasPermission("velocity.add." + serverName)) {
                                        source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cYou do not have permission to add players to the " + serverName + " whitelist."));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    try {
                                        List<String> whitelisted = config.getStringList("servers." + serverName  + ".whitelisted", new ArrayList<>());

                                        if (!whitelisted.contains(playerName)) {
                                            whitelisted.add(playerName);
                                        }

                                        config.set("servers."+ serverName  +".whitelisted", whitelisted);

                                        config.update();
                                        config.save();

                                        source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX + " &7You have successfully &aadded &7" + playerName + " to the " + serverName + " server whitelist.")));
                                    } catch (Exception e) {
                                        source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cAn error occurred while modifying the configuration."));
                                    }

                                    return Command.SINGLE_SUCCESS;

                                }))
                )
                .build();
    }
}
