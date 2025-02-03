package me.gmisi.velocityWhitelist.commands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.CommandHandler;
import me.gmisi.velocityWhitelist.commands.VelocitySubCommand;
import me.gmisi.velocityWhitelist.utils.LanguageManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.IOException;
import java.nio.file.Path;

public class ReloadCommand implements VelocitySubCommand {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final YamlDocument config;
    private final Path dataDirectory;

    public ReloadCommand(YamlDocument config, Path dataDirectory) {
        this.config = config;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {

        return LiteralArgumentBuilder.<CommandSource>literal("reload")
                .executes(context -> {
                    CommandSource source = context.getSource();

                    if (!source.hasPermission(CommandHandler.PERMISSION_ROOT + ".reload")) {
                        source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("reload-no-perm")));
                        return Command.SINGLE_SUCCESS;
                    }

                    try {
                        config.reload();
                        config.update();
                        config.save();

                        VelocityWhitelist.getLang().reload();
                        VelocityWhitelist.getLang().update();
                        VelocityWhitelist.getLang().save();

                        LanguageManager languageManager = new LanguageManager(dataDirectory);
                        if (!config.contains("lang")) {
                            languageManager.loadLanguageFile();
                        }
                        else {
                            String language = config.getString("lang");
                            languageManager.loadLanguageFile(language);
                        }

                        VelocityWhitelist.setLang(languageManager.getLanguageConfig());

                        source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("reload-success"))));
                    } catch (IOException e) {
                        source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " " + VelocityWhitelist.getLang().getString("reload-error")));
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
