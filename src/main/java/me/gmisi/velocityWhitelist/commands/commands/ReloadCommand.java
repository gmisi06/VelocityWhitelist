package me.gmisi.velocityWhitelist.commands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import me.gmisi.velocityWhitelist.commands.VelocitySubCommand;
import me.gmisi.velocityWhitelist.lang.LangKey;
import me.gmisi.velocityWhitelist.utils.ConfigManager;
import me.gmisi.velocityWhitelist.utils.MessageUtil;
import me.gmisi.velocityWhitelist.utils.PermissionUtil;

public class ReloadCommand implements VelocitySubCommand {


    private final ConfigManager configManager;

    public ReloadCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {
        return LiteralArgumentBuilder.<CommandSource>literal("reload")
                .executes(this::executeReload)
                .build();
    }

    /**
     * Executes the {@code /vwl reload} command.
     * <p>
     * This command reloads the plugin's configuration and language files.
     * It performs the following steps:
     * <ul>
     *     <li>Checks if the command source has the global {@code reload} permission.</li>
     *     <li>Reloads the main configuration file using {@link ConfigManager}.</li>
     *     <li>Reads the language code from the newly loaded configuration.</li>
     *     <li>Loads the corresponding language file and updates the global language reference.</li>
     *     <li>Sends a localized success message to the command source.</li>
     * </ul>
     * <p>
     * If the command source lacks permission, an appropriate error message is shown.
     *
     * @param context the {@link CommandContext} containing the command source and arguments
     * @return {@code Command.SINGLE_SUCCESS} indicating the command was processed
     */
    private int executeReload(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        if (!PermissionUtil.hasGlobal(source, "reload")) {
            MessageUtil.sendPrefixed(source, configManager.getLang().get(LangKey.RELOAD_NO_PERM));
            return Command.SINGLE_SUCCESS;
        }

        configManager.reload(source);

        MessageUtil.sendPrefixed(source, configManager.getLang().get(LangKey.RELOAD_SUCCESS));

        return Command.SINGLE_SUCCESS;
    }
}
