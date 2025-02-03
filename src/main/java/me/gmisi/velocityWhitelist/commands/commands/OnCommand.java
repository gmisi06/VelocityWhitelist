package me.gmisi.velocityWhitelist.commands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.CommandHandler;
import me.gmisi.velocityWhitelist.commands.VelocitySubCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class OnCommand implements VelocitySubCommand {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final ProxyServer proxy;
    private final YamlDocument config;

    public OnCommand(ProxyServer proxy, YamlDocument config) {
        this.proxy = proxy;
        this.config = config;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {
        return LiteralArgumentBuilder.<CommandSource>literal("on")
                .executes(context -> {
                    CommandSource source = context.getSource();

                    source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX)));
                    source.sendMessage((serializer.deserialize("&7/vwl on <server> - Turn on the whitelist on the specified server.")));

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
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cThe server '" + serverName + "' does not exist."));
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!source.hasPermission(CommandHandler.PERMISSION_ROOT + ".on.*") && !source.hasPermission(CommandHandler.PERMISSION_ROOT + ".on." + serverName)) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cYou do not have permission to turn on the whitelist on the " + serverName + " server"));
                                return Command.SINGLE_SUCCESS;
                            }

                            try {
                                config.set("servers."+ serverName  +".enabled", true);

                                config.update();
                                config.save();

                                source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX + " &7You have successfully &aenabled&7 the whitelist on the " + serverName + " server.")));
                            } catch (Exception e) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cAn error occurred while modifying the configuration."));
                            }

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
