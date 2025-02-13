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
    public static String PERMISSION_ROOT = "velocitywhitelist";

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy, final String command) {

        LiteralCommandNode<CommandSource> chatNode = BrigadierCommand.literalArgumentBuilder(command)
                .requires(source -> source.hasPermission(PERMISSION_ROOT))
                .executes(context -> {
                    CommandSource source = context.getSource();

                    source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX)));
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-on"))));
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-off"))));
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-add"))));
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-remove"))));
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-status"))));
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-kick"))));
                    source.sendMessage((serializer.deserialize(VelocityWhitelist.getLang().getString("help-reload"))));

                    return Command.SINGLE_SUCCESS;
                })
                .then(new OnCommand(proxy, config).getNode())
                .then(new OffCommand(proxy, config).getNode())
                .then(new AddCommand(proxy, config).getNode())
                .then(new RemoveCommand(proxy, config).getNode())
                .then(new StatusCommand(proxy, config).getNode())
                .then(new ReloadCommand(config).getNode())
                .then(new KickCommand(proxy, config).getNode())
                .build();

        return new BrigadierCommand(chatNode);
    }

}
