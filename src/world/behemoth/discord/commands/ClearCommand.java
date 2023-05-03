package world.behemoth.discord.commands;

import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.world.World;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Arrays;

public class ClearCommand implements IDiscord
{
    @Override
    public void process(World world, MessageCreateEvent event) throws CommandException
    {
        String[] words = {"master", "all", "item", "class", "map", "achievement", "worldboss", "quest", "shop", "settings"};
        String[] command = event.getMessageContent().toLowerCase().split(" ");
        EmbedBuilder embed = new EmbedBuilder();

        if (command.length <= 1) {
            embed.setAuthor("Clear", null, event.getMessageAuthor().getAvatar());
            embed.addField("How to use?", "`$clear <map|shop|quest|item|settings|discord|all>`");
            embed.setFooter("This command is used to clear data server.");
            embed.setColor(Color.BLACK);
            embed.setThumbnail(event.getMessageAuthor().getAvatar());
            event.getChannel().sendMessage(embed);
            return;
        }

        if (Arrays.asList(words).contains(command[1])) {
            world.retrieveDatabaseObject(command[1]);
            embed.setAuthor(event.getMessageAuthor().getDiscriminatedName(), null, event.getMessageAuthor().getAvatar());
            embed.setTitle("INFO");
            embed.setDescription("Server data cleared.");
//            embed.setColor(event.getMessage().getAuthor().getRoleColor().get());
            embed.setThumbnail(event.getMessageAuthor().getAvatar());
            event.getChannel().sendMessage(embed);
        } else {
            event.getChannel().sendMessage("Unknown object type '" + command[1] + "', please type /help for more info.");
        }
    }
}