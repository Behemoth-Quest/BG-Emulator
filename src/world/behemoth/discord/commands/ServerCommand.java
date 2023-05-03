package world.behemoth.discord.commands;

import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import org.javacord.api.event.message.MessageCreateEvent;
import world.behemoth.config.ConfigData;
import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.tasks.Restart;
import world.behemoth.tasks.Shutdown;
import world.behemoth.world.World;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ServerCommand implements IDiscord
{
    @Override
    public void process(World world, MessageCreateEvent event) throws CommandException
    {
        String[] command = event.getMessageContent().toLowerCase().split(" ");
        String[] words = {"restart", "restartnow", "shutdown"};

        if (command.length <= 1) {
            event.getChannel().sendMessage("Missing Argument! try `/help`");
            return;
        }

        if (! Arrays.asList(words).contains(command[1])) {
            event.getChannel().sendMessage("Unknown command type '" + command[1] + "', please type `/help` for more info.");
            return;
        }

        switch (command[1]) {
            case "restart":
                event.getChannel().sendMessage("Restarting the Server " + ConfigData.SERVER_NAME + ". Please logout to prevent data loss.");
                world.scheduleTask(new Restart(world), 0L, TimeUnit.SECONDS);
                break;
            case "restartnow":
                try {
                    event.getChannel().sendMessage("Restarting the server now!");
                    world.send(new String[]{"logoutWarning", "", "60"}, world.zone.getChannelList());
                    world.shutdown();
                    Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
                    ExtensionHelper.instance().rebootServer();
                } catch (InterruptedException e) {
                    event.getChannel().sendMessage("Error!" + e);
                }
                break;
            case "shutdown":
                event.getChannel().sendMessage("The server **" + ConfigData.SERVER_NAME + "** has been scheduled for a server shutdown in 5 minutes. Please logout to prevent data loss.");
                world.scheduleTask(new Shutdown(world), 0L, TimeUnit.SECONDS);
                break;
            default:
                event.getChannel().sendMessage("Unknown command type '" + command[1] + "', please type `/help` for more info.");
                return;
        }
    }
}
