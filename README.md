# VelocityWhitelist
*A Velocity proxy whitelist management plugin.*

## Commands

| Command                       | Description                                          |
|-------------------------------|------------------------------------------------------|
| /vwl on \<server>              | Turn on the whitelist on the specified server.       |
| /vwl off \<server>             | Turn off the whitelist on the specified server.      |
| /vwl add \<player> \<server>    | Add player to the specified server's whitelist.      |
| /vwl remove \<player> \<server> | Remove player from the specified server's whitelist. |
| /vwl status \<server>          | View the status of the specified server whitelist.   |
| /vwl reload                   | Reload configuration.                                |

## Permissions

| Permission                         | Description                                                                                       |
|------------------------------------|---------------------------------------------------------------------------------------------------|
| `velocitywhitelist`                | Grants access to view the plugin's help and basic commands.                                       |
| `velocitywhitelist.on.<server>`    | Allows enabling the whitelist on a specific server (replace `<server>` with the server name).       |
| `velocitywhitelist.on.*`           | Grants global permission to enable the whitelist on any server.                                   |
| `velocitywhitelist.off.<server>`   | Allows disabling the whitelist on a specific server (replace `<server>` with the server name).      |
| `velocitywhitelist.off.*`          | Grants global permission to disable the whitelist on any server.                                  |
| `velocitywhitelist.add.<server>`   | Allows adding a player to the whitelist on a specific server (replace `<server>` with the server name). |
| `velocitywhitelist.add.*`          | Grants global permission to add players to the whitelist on any server.                           |
| `velocitywhitelist.remove.<server>`| Allows removing a player from the whitelist on a specific server (replace `<server>` with the server name).|
| `velocitywhitelist.remove.*`       | Grants global permission to remove players from the whitelist on any server.                      |
| `velocitywhitelist.status.<server>`| Allows viewing the whitelist status on a specific server (replace `<server>` with the server name).  |
| `velocitywhitelist.status.*`       | Grants global permission to view the whitelist status on any server.                              |
| `velocitywhitelist.reload`         | Allows reloading the plugin configuration.                                                      |
| `velocitywhitelist.bypass`         | Allows the player to bypass whitelist restrictions, regardless of server-specific settings.       |

