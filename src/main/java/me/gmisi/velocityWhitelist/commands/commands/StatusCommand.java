package me.gmisi.velocityWhitelist.commands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.VelocitySubCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class StatusCommand implements VelocitySubCommand {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final ProxyServer proxy;
    private final YamlDocument config;

    public StatusCommand(ProxyServer proxy, YamlDocument config) {
        this.proxy = proxy;
        this.config = config;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {
        return LiteralArgumentBuilder.<CommandSource>literal("status")
                .executes(context -> {
                    CommandSource source = context.getSource();

                    source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX)));
                    source.sendMessage((serializer.deserialize("&7/vwl status <server> - View the status of the specified server whitelist.")));

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

                            if (!source.hasPermission("velocity.status.*") && !source.hasPermission("velocity.status." + serverName)) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cYou do not have permission to view the status of the " + serverName + " server whitelist."));
                                return Command.SINGLE_SUCCESS;
                            }

                            try {
                                boolean enabled = (boolean) config.get("servers."+ serverName  +".enabled");
                                List<String> whitelisted = config.getStringList("servers." + serverName  + ".whitelisted", new ArrayList<>());

                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &7Whitelist Status for &b" + serverName + "&7: "));
                                source.sendMessage(serializer.deserialize("&7Enabled: &b" + enabled));
                                source.sendMessage(serializer.deserialize("&7Players whitelisted:"));

                                whitelisted.forEach(player -> source.sendMessage(serializer.deserialize("  &7- &b" + player)));

                            } catch (Exception e) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cAn error occurred while loading the configuration."));
                            }

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

}
