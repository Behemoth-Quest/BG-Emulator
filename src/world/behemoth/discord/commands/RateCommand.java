package world.behemoth.discord.commands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.util.logging.ExceptionLogger;
import world.behemoth.config.ConfigData;
import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.world.World;

import java.awt.*;
import java.util.Arrays;

public class RateCommand implements IDiscord
{
    public void process(World world, MessageCreateEvent event) throws CommandException
    {
        String[] command = event.getMessageContent().toLowerCase().split(" ");
        String[] words = {"exp", "cp", "gold", "coin", "rep", "drop", "all"};

        if (command.length <= 1) {
            event.getChannel().sendMessage("missing argument! try /help");
            return;
        }

        if (! Arrays.asList(words).contains(command[1])) {
            event.getChannel().sendMessage("Unknown rates type '" + command[1] + "', please type /help for more info.");
            return;
        }

        try {
            String type = world.getServerRates(command[1].toLowerCase());
            world.updateServerRates(command[1].toLowerCase(), Integer.valueOf(command[2]));
            world.sendServerMessage(type + " rates has been changed to x" + Integer.valueOf(command[2]));
            event.getChannel().sendMessage(type + " rates has been changed to x" + command[2]);
        } catch (Exception e) {
            event.getChannel().sendMessage("Error! " + e);
        }
    }
}
