package world.behemoth.discord.commands;

import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import world.behemoth.config.ConfigData;
import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.tasks.Restart;
import world.behemoth.world.World;

import java.util.concurrent.TimeUnit;

public class RestartCommand implements IDiscord
{
    @Override
    public void process(World world, MessageCreateEvent event) throws CommandException
    {
        String[] command = event.getMessageContent().toLowerCase().split(" ");

//        try {
//            event.getChannel().sendMessage("Restarting the Server " + ConfigData.SERVER_NAME + ". Please logout to prevent data loss.");
//            Restart restart = new Restart(world);
//            restart.setRunning(world.scheduleTask(restart, 0L, TimeUnit.SECONDS));
//        } catch (Exception e) {
//            event.getChannel().sendMessage("Error!" + e);
//        }

        try {
            event.getChannel().sendMessage("Restarting the Server " + ConfigData.SERVER_NAME + ". Please logout to prevent data loss.");
            world.send(new String[]{"logoutWarning", "", "60"}, world.zone.getChannelList());
            world.shutdown();
            Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
            ExtensionHelper.instance().rebootServer();
        } catch (InterruptedException e) {
            event.getChannel().sendMessage("Error!" + e);
        }
    }
}
