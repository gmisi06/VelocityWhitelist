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

import java.util.List;
import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class StatusCommand implements VelocitySubCommand {

    private final ProxyServer proxy;
    private final YamlDocument config;
    private final ConfigManager configManager;

    public StatusCommand(ProxyServer proxy, ConfigManager configManager) {
        this.proxy = proxy;
        this.config = configManager.getConfig();
        this.configManager = configManager;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {

        return LiteralArgumentBuilder.<CommandSource>literal("status")
                .executes(this::sendHelp)
                .then(buildServerArg())
                .build();
    }

    /**
     * Sends the help message for the <code>/vwl status</code> command to the command source.
     * This includes the plugin prefix and the localized help text from the language file.
     *
     * @param context the command execution context
     * @return {@code Command.SINGLE_SUCCESS} to indicate successful execution
     */
    private int sendHelp(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MessageUtil.sendPrefix(source);
        MessageUtil.sendNotPrefixed(source, configManager.getLang().get(LangKey.HELP_STATUS));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Builds the {@code server} argument node and attaches suggestion and execution logic.
     */
    private RequiredArgumentBuilder<CommandSource, String> buildServerArg() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("server", word())
                .suggests((context, builder) -> SuggestUtil.serverSuggests(proxy, builder))
                .executes(this::executeStatus);
    }

    /**
     * Executes the {@code /vwl remove <player> <server>} command.
     * <p>
     * This command removes a specified player from the whitelist of the given server.
     * Before removing, it checks whether the target server exists and if the command source
     * has the appropriate permission to perform the removal.
     * <p>
     * If the server is not found, or the source lacks permission, the method sends an appropriate
     * localized error message to the source and terminates.
     * <p>
     * Upon successful removal, the configuration is updated and saved,
     * and a success message is sent. If an error occurs during the modification process,
     * a generic failure message is displayed instead.
     *
     * @param context the command context containing the parsed arguments, including {@code player} and {@code server}
     * @return {@code Command.SINGLE_SUCCESS} indicating that the command has completed processing
     */
    private int executeStatus(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        String serverName = context.getArgument("server", String.class);

        Map<String, String> placeholders = Map.of(
                "server", serverName
        );

        if (!config.contains("servers." + serverName)) {
            MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.SERVER_NOT_EXISTS, placeholders));
            return Command.SINGLE_SUCCESS;
        }

        if (!PermissionUtil.hasServerPermission(source, "status", serverName)) {
            MessageUtil.sendPrefixed(source, configManager.getLang().get(LangKey.STATUS_NO_PERM));
            return Command.SINGLE_SUCCESS;
        }

        try {
            boolean enabled = configManager.getWhitelistManager().isWhitelistEnabled(serverName);
            List<String> whitelisted = configManager.getWhitelistManager().getWhitelistedPlayers(serverName);

            MessageUtil.sendPrefixed(source, configManager.getLang().getFormatted(LangKey.STATUS_HEADER, placeholders));
            MessageUtil.sendNotPrefixed(source, configManager.getLang().get(LangKey.STATUS_ENABLED) + (enabled ? configManager.getLang().get(LangKey.STATUS_YES): configManager.getLang().get(LangKey.STATUS_NO) ));
            MessageUtil.sendNotPrefixed(source, configManager.getLang().get(LangKey.STATUS_PLAYERS));

            whitelisted.forEach(player -> MessageUtil.sendNotPrefixed(source, "  &7- &b" + player));

        } catch (Exception e) {
            MessageUtil.sendPrefixed(source, configManager.getLang().get(LangKey.LOAD_ERROR));
        }

        return Command.SINGLE_SUCCESS;
    }

}
