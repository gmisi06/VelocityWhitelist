package me.gmisi.velocityWhitelist.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;

public interface VelocitySubCommand {
    LiteralCommandNode<CommandSource> getNode();
}
