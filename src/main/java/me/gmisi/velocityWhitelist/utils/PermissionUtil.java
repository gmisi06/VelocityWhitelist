package me.gmisi.velocityWhitelist.utils;

import com.velocitypowered.api.command.CommandSource;

public class PermissionUtil {

    private final static String PERMISSION_ROOT = "velocitywhitelist";

    public static boolean hasServerPermission(CommandSource source, String action, String server) {
        String base = PERMISSION_ROOT + "." + action;

        return source.hasPermission(base) ||
                source.hasPermission(base + "." + server) ||
                source.hasPermission(base + ".*") ||
                source.hasPermission(PERMISSION_ROOT + ".*");
    }

    public static boolean hasGlobal(CommandSource source, String action) {
        return source.hasPermission(PERMISSION_ROOT + "." + action) ||
                source.hasPermission(PERMISSION_ROOT + ".*");
    }
}
