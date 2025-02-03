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
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-status"))));

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

                            if (!source.hasPermission(CommandHandler.PERMISSION_ROOT + ".status.*") && !source.hasPermission(CommandHandler.PERMISSION_ROOT + ".status." + serverName)) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("status-no-perm")
                                        .replace("{server}", serverName)));
                                return Command.SINGLE_SUCCESS;
                            }

                            try {
                                boolean enabled = (boolean) config.get("servers."+ serverName  +".enabled");
                                List<String> whitelisted = config.getStringList("servers." + serverName  + ".whitelisted", new ArrayList<>());

                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("status-header")
                                        .replace("{server}", serverName)));
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.getLang().getString("status-enabled") + enabled));
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.getLang().getString("status-players") + whitelisted));

                                whitelisted.forEach(player -> source.sendMessage(serializer.deserialize("  &7- &b" + player)));

                            } catch (Exception e) {
                                source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("load-error")));
                            }

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

}
