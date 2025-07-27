package me.gmisi.velocityWhitelist.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WhitelistManager {

    private final YamlDocument config;
    private final Logger logger;

    public WhitelistManager(YamlDocument config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    /**
     * Adds a player to the whitelist of a given server.
     *
     * @param serverName the name of the server
     * @param playerName the player to add to the whitelist
     * @return true if the player was added successfully or already in the list; false if an error occurred
     */
    public boolean addPlayer(String serverName, String playerName) {
        try {
            List<String> whitelisted = config.getStringList("servers." + serverName + ".whitelisted", new ArrayList<>());
            if (!whitelisted.contains(playerName)) {
                whitelisted.add(playerName);
                config.set("servers." + serverName + ".whitelisted", whitelisted);
                config.update();
                config.save();
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to add player to whitelist: ", e);
            return false;
        }
    }

    /**
     * Removes a player from the whitelist of a given server.
     *
     * @param serverName the name of the server
     * @param playerName the player to remove from the whitelist
     * @return true if the player was removed successfully; false if an error occurred
     */
    public boolean removePlayer(String serverName, String playerName) {
        try {
            List<String> whitelisted = config.getStringList("servers." + serverName  + ".whitelisted", new ArrayList<>());
            whitelisted.remove(playerName);

            config.set("servers."+ serverName  +".whitelisted", whitelisted);
            config.update();
            config.save();

            return true;
        } catch (Exception e) {
            logger.error("Failed to remove player to whitelist: ", e);
            return false;
        }
    }

    /**
     * Enables the whitelist on the specified server.
     *
     * @param serverName the name of the server
     * @return true if the operation was successful; false otherwise
     */
    public boolean on(String serverName) {
        try {
            config.set("servers."+ serverName  +".enabled", true);
            config.update();
            config.save();

            return true;
        } catch (Exception e) {
            logger.error("Failed to turn on the whitelist: ", e);
            return false;
        }
    }

    /**
     * Disables the whitelist on the specified server.
     *
     * @param serverName the name of the server
     * @return true if the operation was successful; false otherwise
     */
    public boolean off(String serverName) {
        try {
            config.set("servers."+ serverName  +".enabled", false);
            config.update();
            config.save();

            return true;
        } catch (Exception e) {
            logger.error("Failed to turn off the whitelist: ", e);
            return false;
        }
    }

    /**
     * Retrieves the list of whitelisted players for a given server.
     *
     * @param serverName the name of the server
     * @return a list of player names currently on the whitelist
     */
    public List<String> getWhitelistedPlayers(String serverName) {
        return config.getStringList("servers." + serverName + ".whitelisted", new ArrayList<>());
    }

    /**
     * Checks whether the whitelist is currently enabled for a given server.
     *
     * @param serverName the name of the server
     * @return true if the whitelist is enabled; false otherwise
     */
    public boolean isWhitelistEnabled(String serverName) {
        return config.getBoolean("servers."+ serverName + ".enabled", false);
    }

}
