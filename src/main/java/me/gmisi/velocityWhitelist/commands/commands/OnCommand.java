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

public class OnCommand implements VelocitySubCommand {

    private final ProxyServer proxy;
    private final YamlDocument config;
    private final ConfigManager configManager;

    public OnCommand(ProxyServer proxy, ConfigManager configManager) {
        this.proxy = proxy;
        this.config = configManager.getConfig();
        this.configManager = configManager;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {

        return LiteralArgumentBuilder.<CommandSource>literal("on")
                .executes(this::sendHelp)
                .then(buildServerArg())
                .build();
    }

    /**
     * Sends the help message for the <code>/vwl on</code> command to the command source.
     * This includes the plugin prefix and the localized help text from the language file.
     *
     * @param context the command execution context
     * @return {@code Command.SINGLE_SUCCESS} to indicate successful execution
     */
    private int sendHelp(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MessageUtil.sendPrefix(source);
        MessageUtil.sendNotPrefixed(source, configManager.getLang().get(LangKey.HELP_ON));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Builds the {@code server} argument node and attaches suggestion and execution logic.
     */
    private RequiredArgumentBuilder<CommandSource, String> buildServerArg() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("server", word())
                .suggests((context, builder) -> SuggestUtil.serverSuggests(proxy, builder))
                .executes(this::executeOn);
    }

    /**
     * Executes the {@code /vwl on <server>} command.
     * <p>
     * This command enables the whitelist for the specified server by setting
     * the {@code enabled} flag to {@code true} in the configuration file.
     * <p>
     * The method performs the following steps:
     * <ul>
     *     <li>Retrieves the {@code server} argument from the command context.</li>
     *     <li>Checks if the specified server exists in the configuration.</li>
     *     <li>Verifies if the command sender has permission to enable the whitelist for that server.</li>
     *     <li>If validations pass, updates the server configuration and saves it to disk.</li>
     *     <li>Sends appropriate success or error messages to the command source.</li>
     * </ul>
     *
     * @param context the {@link CommandContext} containing the command source and arguments
     * @return {@code Command.SINGLE_SUCCESS} to indicate that the command was handled,
     *         regardless of whether the operation was successful or rejected due to validation
     */
    private int executeOn(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        String serverName = context.getArgument("server", String.class);
        Map<String, String> placeholders = Map.of(
                "server", serverName
        );

        if (!config.contains("servers." + serverName)) {
            MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.SERVER_NOT_EXISTS, placeholders));
            return Command.SINGLE_SUCCESS;
        }

        if (!PermissionUtil.hasServerPermission(source, "on", serverName)) {;
            MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.ON_NO_PERM, placeholders));
            return Command.SINGLE_SUCCESS;
        }

        boolean success = configManager.getWhitelistManager().on(serverName);
        if (!success) {
            MessageUtil.sendPrefixed(source, configManager.getLang().get(LangKey.MODIFICATION_ERROR));
        }
        MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.ON_SUCCESS, placeholders));

        return Command.SINGLE_SUCCESS;
    }
}
