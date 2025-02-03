package me.gmisi.velocityWhitelist.commands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.gmisi.velocityWhitelist.VelocityWhitelist;
import me.gmisi.velocityWhitelist.commands.VelocitySubCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.IOException;

public class ReloadCommand implements VelocitySubCommand {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    private final YamlDocument config;

    public ReloadCommand(YamlDocument config) {
        this.config = config;
    }

    @Override
    public LiteralCommandNode<CommandSource> getNode() {
        return LiteralArgumentBuilder.<CommandSource>literal("reload")
                .executes(context -> {
                    CommandSource source = context.getSource();

                    if (!source.hasPermission("velocity.reload")) {
                        source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cYou do not have permission to reload the configuration."));
                        return Command.SINGLE_SUCCESS;
                    }

                    try {
                        config.reload();
                        config.update();
                        config.save();

                        source.sendMessage((serializer.deserialize(VelocityWhitelist.PREFIX + " &7Configuration successfully reloaded!")));
                    } catch (IOException e) {
                        source.sendMessage(serializer.deserialize(VelocityWhitelist.PREFIX + " &cAn error occurred while reloading the configuration."));
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
