package me.gmisi.velocityWhitelist.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import me.gmisi.velocityWhitelist.commands.commands.*;
import me.gmisi.velocityWhitelist.lang.LangKey;
import me.gmisi.velocityWhitelist.utils.ConfigManager;
import me.gmisi.velocityWhitelist.utils.MessageUtil;

import java.util.List;


public class CommandHandler {

    public final static String PERMISSION_ROOT = "velocitywhitelist";

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy, final String command, ConfigManager configManager) {

        LiteralCommandNode<CommandSource> chatNode = BrigadierCommand.literalArgumentBuilder(command)
                .requires(source -> source.hasPermission(PERMISSION_ROOT))
                .executes(context -> {
                    CommandSource source = context.getSource();

                    List<LangKey.LangEntry> helpKeys = List.of(
                            LangKey.HELP_ON,
                            LangKey.HELP_OFF,
                            LangKey.HELP_ADD,
                            LangKey.HELP_REMOVE,
                            LangKey.HELP_STATUS,
                            LangKey.HELP_KICK,
                            LangKey.HELP_RELOAD
                    );

                    MessageUtil.sendPrefix(source);
                    for (LangKey.LangEntry key : helpKeys) {
                        MessageUtil.sendNotPrefixed(source, configManager.getLang().get(key));
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .then(new OnCommand(proxy, configManager).getNode())
                .then(new OffCommand(proxy, configManager).getNode())
                .then(new AddCommand(proxy, configManager).getNode())
                .then(new RemoveCommand(proxy, configManager).getNode())
                .then(new StatusCommand(proxy, configManager).getNode())
                .then(new ReloadCommand(configManager).getNode())
                .then(new KickCommand(proxy, configManager).getNode())
                .build();

        return new BrigadierCommand(chatNode);
    }

}
