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

public class OffCommand implements VelocitySubCommand {


    private final ProxyServer proxy;
    private final YamlDocument config;
    private final ConfigManager configManager;

    public OffCommand(ProxyServer proxy, ConfigManager configManager) {
        this.proxy = proxy;
        this.config = configManager.getConfig();
        this.configManager = configManager;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {

        return LiteralArgumentBuilder.<CommandSource>literal("off")
                .executes(this::sendHelp)
                .then(buildServerArg())
                .build();
    }

    /**
     * Sends the help message for the <code>/vwl off</code> command to the command source.
     * This includes the plugin prefix and the localized help text from the language file.
     *
     * @param context the command execution context
     * @return {@code Command.SINGLE_SUCCESS} to indicate successful execution
     */
    private int sendHelp(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MessageUtil.sendPrefix(source);
        MessageUtil.sendNotPrefixed(source, configManager.getLang().get(LangKey.HELP_OFF));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Builds the {@code server} argument node and attaches suggestion and execution logic.
     */
    private RequiredArgumentBuilder<CommandSource, String> buildServerArg() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("server", word())
                .suggests((context, builder) -> SuggestUtil.serverSuggests(proxy, builder))
                .executes(this::executeOff);
    }

    /**
     * Executes the {@code /vwl off <server>} command.
     * <p>
     * This command disables the whitelist for a specific server by setting
     * the {@code enabled} flag to {@code false} in the configuration file.
     * <p>
     * The method performs the following steps:
     * <ul>
     *     <li>Retrieves the server name argument.</li>
     *     <li>Checks whether the specified server exists in the configuration.</li>
     *     <li>Validates whether the sender has permission to perform the operation.</li>
     *     <li>If validation passes, disables the whitelist by updating and saving the config.</li>
     *     <li>Sends appropriate success or error messages to the command source.</li>
     * </ul>
     *
     * @param context the {@link CommandContext} containing the command source and arguments
     * @return {@code Command.SINGLE_SUCCESS} to indicate the command was processed,
     *         regardless of outcome (success or permission/validation failure)
     */
    private int executeOff(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        String serverName = context.getArgument("server", String.class);
        Map<String, String> placeholders = Map.of(
                "server", serverName
        );

        if (!config.contains("servers." + serverName)) {
            MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.SERVER_NOT_EXISTS, placeholders));
            return Command.SINGLE_SUCCESS;
        }

        if (!PermissionUtil.hasServerPermission(source, "off", serverName)) {
            MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.OFF_NO_PERM, placeholders));
            return Command.SINGLE_SUCCESS;
        }

        boolean success = configManager.getWhitelistManager().off(serverName);
        if (!success) {
            MessageUtil.sendPrefixed(source, configManager.getLang().get(LangKey.MODIFICATION_ERROR));
        }
        MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.OFF_SUCCESS, placeholders));

        return Command.SINGLE_SUCCESS;
    }
}
