package me.gmisi.velocityWhitelist.lang;

public class LangKey {

    public static final LangEntry SERVER_NOT_EXISTS = new LangEntry("server-not-exists", "&cThe server '{server}' does not exist.");
    public static final LangEntry MODIFICATION_ERROR = new LangEntry("modification-error", "&cAn error occurred while modifying the configuration.");
    public static final LangEntry RELOAD_ERROR = new LangEntry("reload-error", "&cAn error occurred while reloading the configuration.");
    public static final LangEntry LOAD_ERROR = new LangEntry("load-error", "&cAn error occurred while loading the configuration.");

    public static final LangEntry ON_SUCCESS = new LangEntry("on-success", "&7You have successfully &aenabled&7 the whitelist on the {server} server.");
    public static final LangEntry ON_NO_PERM = new LangEntry("on-no-perm", "&cYou do not have permission to turn on the whitelist on the {server} server.");

    public static final LangEntry OFF_SUCCESS = new LangEntry("off-success", "&7You have successfully &cdisabled&7 the whitelist on the {server} server.");
    public static final LangEntry OFF_NO_PERM = new LangEntry("off-no-perm", "&cYou do not have permission to turn off the whitelist on the {server} server.");

    public static final LangEntry ADD_SUCCESS = new LangEntry("add-success", "&7You have successfully &aadded &7{player} to the {server} server whitelist.");
    public static final LangEntry ADD_NO_PERM = new LangEntry("add-no-perm", "&cYou do not have permission to add players to the {server} whitelist.");

    public static final LangEntry REMOVE_SUCCESS = new LangEntry("remove-success", "&7You have successfully &cremoved &7{player} from the {server} server whitelist.");
    public static final LangEntry REMOVE_NO_PERM = new LangEntry("remove-no-perm", "&cYou do not have permission to remove players from the {server} whitelist.");

    public static final LangEntry STATUS_NO_PERM = new LangEntry("status-no-perm", "&cYou do not have permission to view the status of the {server} server whitelist.");
    public static final LangEntry STATUS_HEADER = new LangEntry("status-header", "&7Whitelist Status for &b{server}&7:");
    public static final LangEntry STATUS_ENABLED = new LangEntry("status-enabled", "&7Enabled: &b");
    public static final LangEntry STATUS_PLAYERS = new LangEntry("status-players", "&7Players whitelisted:");
    public static final LangEntry STATUS_YES = new LangEntry("status-yes", "&byes");
    public static final LangEntry STATUS_NO = new LangEntry("status-no", "&bno");

    public static final LangEntry RELOAD_SUCCESS = new LangEntry("reload-success", "&7Configuration successfully reloaded!");
    public static final LangEntry RELOAD_NO_PERM = new LangEntry("reload-no-perm", "&cYou do not have permission to reload the configuration.");

    public static final LangEntry KICK_SUCCESS = new LangEntry("kick-success", "&7You have successfully &ckicked non-whitelisted players from the {server} server.");
    public static final LangEntry KICK_NO_PERM = new LangEntry("kick-no-perm", "&cYou do not have permission to kick the players.");

    public static final LangEntry HELP_ON = new LangEntry("help-on", "&7/vwl on <server> - Turn on the whitelist on the specified server.");
    public static final LangEntry HELP_OFF = new LangEntry("help-off", "&7/vwl off <server> - Turn off the whitelist on the specified server.");
    public static final LangEntry HELP_ADD = new LangEntry("help-add", "&7/vwl add <player> <server> - Add player to the specified server's whitelist.");
    public static final LangEntry HELP_REMOVE = new LangEntry("help-remove", "&7/vwl remove <player> <server> - Remove player from the specified server's whitelist.");
    public static final LangEntry HELP_STATUS = new LangEntry("help-status", "&7/vwl status <server> - View the status of the specified server whitelist.");
    public static final LangEntry HELP_KICK = new LangEntry("help-kick", "&7/vwl kick <server> - Kicking players not on the whitelist from the specified server.");
    public static final LangEntry HELP_RELOAD = new LangEntry("help-reload", "&7/vwl reload - Reload configuration.");

    public static class LangEntry {
        public final String path;
        public final String defaultValue;

        public LangEntry(String path, String defaultValue) {
            this.path = path;
            this.defaultValue = defaultValue;
        }
    }
}
