package world.behemoth.discord.commands;

import world.behemoth.discord.events.ReloadSettings;
import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.world.World;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class Prefix implements IDiscord
{
    @Override
    public void process(World world, MessageCreateEvent event) throws CommandException
    {
        String[] command = event.getMessageContent().split(" ");
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

        world.db.jdbc.run("UPDATE discords_settings SET Value = ? WHERE Key = ?", command[1], "Prefix");
        new ReloadSettings(world);
        event.getChannel().sendMessage("Bot prefix set to " + command[1]);
    }
}
