package me.gmisi.velocityWhitelist.commands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.commands.SuggestUtil;
import me.gmisi.velocityWhitelist.commands.VelocitySubCommand;
import me.gmisi.velocityWhitelist.lang.LangKey;
import me.gmisi.velocityWhitelist.utils.WhitelistManager;
import me.gmisi.velocityWhitelist.utils.ConfigManager;
import me.gmisi.velocityWhitelist.utils.MessageUtil;
import me.gmisi.velocityWhitelist.utils.PermissionUtil;

import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class RemoveCommand implements VelocitySubCommand {

    private final ProxyServer proxy;
    private final YamlDocument config;
    private final ConfigManager configManager;

    public RemoveCommand(ProxyServer proxy, ConfigManager configManager) {
        this.proxy = proxy;
        this.config = configManager.getConfig();
        this.configManager = configManager;
    }
    
    @Override
    public LiteralCommandNode<CommandSource> getNode() {
        return LiteralArgumentBuilder.<CommandSource>literal("remove")
                .executes(this::sendHelp)
                .then(buildPlayerArg())
                .build();
    }

    /**
     * Sends the help message for the <code>/vwl remove</code> command to the command source.
     * This includes the plugin prefix and the localized help text from the language file.
     *
     * @param context the command execution context
     * @return {@code Command.SINGLE_SUCCESS} to indicate successful execution
     */
    private int sendHelp(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MessageUtil.sendPrefix(source);
        MessageUtil.sendNotPrefixed(source, configManager.getLang().get(LangKey.HELP_REMOVE));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Builds the {@code player} argument node and attaches suggestion and execution logic.
     */
    private RequiredArgumentBuilder<CommandSource, String> buildPlayerArg() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("player", word())
                .suggests((context, builder) -> SuggestUtil.playerSuggests(proxy, builder))
                .then(buildServerArg());
    }

    /**
     * Builds the {@code server} argument node and attaches suggestion and execution logic.
     */
    private RequiredArgumentBuilder<CommandSource, String> buildServerArg() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("server", word())
                .suggests((context, builder) -> SuggestUtil.serverSuggests(proxy, builder))
                .executes(this::executeRemove);
    }

    /**
     * Executes the {@code /vwl remove <player> <server>} command.
     * <p>
     * Validates the target server, checks the sender's permissions,
     * and removes the specified player from the whitelist of the given server.
     * If the server or player does not exist, or the sender lacks permission,
     * appropriate feedback is sent. Configuration is updated and saved after removal.
     *
     * @param context the command context containing the parsed arguments
     * @return {@code Command.SINGLE_SUCCESS} indicating the command was processed
     */
    private int executeRemove(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        String playerName = context.getArgument("player", String.class);
        String serverName = context.getArgument("server", String.class);

        Map<String, String> placeholders = Map.of(
                "server", serverName,
                "player", playerName
        );

        if (!config.contains("servers." + serverName)) {
            MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.SERVER_NOT_EXISTS, placeholders));
            return Command.SINGLE_SUCCESS;
        }

        if (!PermissionUtil.hasServerPermission(source, "remove", serverName)) {
            MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.REMOVE_NO_PERM, placeholders));
            return Command.SINGLE_SUCCESS;
        }

        boolean success = configManager.getWhitelistManager().removePlayer(serverName, playerName);
        if (!success) {
            MessageUtil.sendPrefixed(source, configManager.getLang().get(LangKey.MODIFICATION_ERROR));
        }

        MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.REMOVE_SUCCESS, placeholders));

        return Command.SINGLE_SUCCESS;
    }
}
