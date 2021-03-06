# SimpleVoteSystem

This plugin is a voting plugin designed to be put on a BungeeCord server, its purpose is to provide compatibility for voting sites, which do not support votifier.


## What voting sites are currently supported?

- https://serveur-prive.net/
- https://www.serveurs-minecraft.org/
- https://www.serveursminecraft.org/
- https://www.liste-serveurs-minecraft.org/
- www.liste-serveurs.fr
- https://www.liste-serveur.fr/

## As a server owner, how can I install the plugin on my server?

### I advise you to divide the installation process in three steps:

#### The first one is to configure the database
So you have to go to the config.yml file generated by the plugin at the first start, and look at the `database` key:
```yaml
database:
  host: localhost
  port: 3306
  database: db
  username: user 
  password: passw
```

You will have to change the accesses indicated here by the real accesses to your database.

#### The second step is to configure the votes websites
You have to look at the `websites` key of the configuration file, the typical configuration for a voting site looks like this:
```yaml
serveur_PriveNet:
  enabled: true
  votePage: 'https://serveur-prive.net/minecraft/ivernya-3422'
  serverId: '5GHP0copFW7AlCi'
```
There are 3 particular values:
- `enabled`: this allows you to enable or disable one of the voting sites supported by the plugin.
- `votePage`: This is absolutely visual, it is the voting link that your players will find when they drag the mouse over the name of the voting site in the voting command.
- `serverId`: This value is very important, it's the ID of your server, or the vote token depending on the voting site, which will allow the plugin to detect the vote with the API of the voting website.

#### The third step is to configure the rewards
The reward system works through a system of votes "chain". You can define them at the `rewards` key in the configuration file and define the command list assigned to that chain of votes:
```yaml
rewards:
  1:
    - 'alert %playerName% voted on %websiteName%'
  2:
    - 'alert %playerName% voted on %websiteName%, chain of 2 votes'
  3:
    - 'alert %playerName% voted on %websiteName%, chain of 3 votes'
```

As you can see here, the system works with bungeecord commands, so if you want to give a reward you need to use a plugin able to execute bukkit commands from the bungeecord server.

##### Then you are good to go.

#### The votes cleaner
Now maybe you are wondering what is the `useCleaner` key in configuration, it allows your server, if set to true, to clean all the vote counts every first day of a new month.

## As a vote website owner, how can I add my website?
You need to add a class in the websites package that extends from the AbstractHasVoted abstract class, it contains the following abstract methods which you will have to implement:

```java 
    /**
     * @param player The player whose IP we want to check on the voting website
     * @return The time before the next vote in seconds if a vote is detected, or -1 if no vote is detected.
     */
    public abstract int hasVoted(ProxiedPlayer player);

    /**
     * @return The website name as it should be in the configuration and database.
     */
    public abstract String getWebsiteName();

    /**
     * @return The website name as it should be displayed in the voting comand, and the websiteName placeholder
     */
    public abstract String getUserFriendlyName();

    /**
     * @param serverId The ID or the token of the server on the voting website
     * @param playerIp The IP address we want to verify
     * @return
     */
    public abstract String getUrl(String serverId, String playerIp);
```

The important thing to remember that you have to implement is the hasVoted abstract method, it's really simple, it should typically look like this if your API is ideal for the system:

```java
    @Override
    public int hasVoted(ProxiedPlayer player) {
        JSONObject result = ReadersUtil.readJsonFromUrl(getUrl(getServerIdForWebsite(), player.getAddress().getHostString()));
        if (result.getInt("status") == 1) {
            return result.getInt("nextvote");
        } else {
            return -1;
        }
    }
```


If the IP in question has already voted on your website, you have to return the time in seconds left before the next vote.

If you don't detect any vote on that IP, then return -1.
