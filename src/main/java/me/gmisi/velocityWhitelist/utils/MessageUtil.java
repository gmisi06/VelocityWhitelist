package me.gmisi.velocityWhitelist.utils;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {
    private static final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    public static void sendPrefixed(CommandSource source, String message) {
        String prefix = ConfigManager.getInstance().get("prefix", "&9&l[VelocityWhitelist]");
        source.sendMessage(serializer.deserialize(prefix + " " + message));
    }

    public static TextComponent getPrefixed(String message) {
        String prefix = ConfigManager.getInstance().get("prefix", "&9&l[VelocityWhitelist]");
        return serializer.deserialize(prefix + " " + message);
    }

    public static void sendNotPrefixed(CommandSource source, String message) {
        source.sendMessage(serializer.deserialize(message));
    }

    public static void sendPrefix(CommandSource source) {
        String prefix = ConfigManager.getInstance().get("prefix", "&9&l[VelocityWhitelist]");
        source.sendMessage(serializer.deserialize(prefix ));
    }

}