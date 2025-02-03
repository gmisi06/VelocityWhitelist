package me.gmisi.velocityWhitelist.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.commands.*;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;


public class CommandHandler {

    private final static LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
    private final static YamlDocument config = VelocityWhitelist.getConfig();

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy, final String command) {

        LiteralCommandNode<CommandSource> chatNode = BrigadierCommand.literalArgumentBuilder(command)
                .requires(source -> source.hasPermission("velocitywhitelist"))
                .executes(context -> {
                    CommandSource source = context.getSource();

                    source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX)));
                    source.sendMessage((serializer.deserialize("&7/vwl on <server> - Turn on the whitelist on the specified server.")));
                    source.sendMessage((serializer.deserialize("&7/vwl off <server> - Turn off the whitelist on the specified server.")));
                    source.sendMessage((serializer.deserialize("&7/vwl add <player> <server> - Add player to the specified server's whitelist.")));
                    source.sendMessage((serializer.deserialize("&7/vwl remove <player> <server> - Remove player from the specified server's whitelist.")));
                    source.sendMessage((serializer.deserialize("&7/vwl status <server> - View the status of the specified server whitelist.")));
                    source.sendMessage((serializer.deserialize("&7/vwl reload - Reload configuration.")));

                    return Command.SINGLE_SUCCESS;
                })
                .then(new OnCommand(proxy, config).getNode())
                .then(new OffCommand(proxy, config).getNode())
                .then(new AddCommand(proxy, config).getNode())
                .then(new RemoveCommand(proxy, config).getNode())
                .then(new StatusCommand(proxy, config).getNode())
                .then(new ReloadCommand(config).getNode())
                .build();

        return new BrigadierCommand(chatNode);
    }

}
