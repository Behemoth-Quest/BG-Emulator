package world.behemoth.discord;

import it.gotoandplay.smartfoxserver.SmartFoxServer;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import world.behemoth.db.objects.DiscordCommand;
import world.behemoth.discord.events.ReloadSettings;
import world.behemoth.discord.utils.Listener;
import world.behemoth.world.World;

import java.util.HashMap;

public class Bot {
    public DiscordApi api;
    public World world;

    public HashMap<Integer, DiscordCommand> commands;
    public static HashMap<String, String> settings = new HashMap();

    public Bot(String token, World world){
        this.world = world;
        new ReloadSettings(world);
        this.init(token);
    }

    private void init (String token) {
        FallbackLoggerConfiguration.setDebug(false);
        api = new DiscordApiBuilder().setToken(token).login().join();
//        api.updateActivity(ActivityType.PLAYING, ConfigData.DISCORD_BOT_STATUS);
        api.addMessageCreateListener(new Listener(this.world));
        SmartFoxServer.log.info("Discord Bot Initialized.");
    }

    public String getPrefix() {
        return Bot.settings.get("Prefix");
    }
}
