package world.behemoth.discord.commands;

import jdbchelper.QueryResult;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import world.behemoth.config.ConfigData;
import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapCommand implements IDiscord
{
    @Override
    public void process(World world, MessageCreateEvent event) throws CommandException
    {
        String[] command = event.getMessageContent().toLowerCase().split(" ");
        EmbedBuilder embed = new EmbedBuilder();

        if (command.length <= 1) {
//            event.getChannel().sendMessage("input user id");

            embed.setAuthor(event.getMessageAuthor().getDiscriminatedName(), null, event.getMessageAuthor().getAvatar());
            embed.addField("How to use?", world.discord.getPrefix() + "`map [MAP NAME]`");
            embed.setFooter("This command is used to search map by name.");
//            embed.setColor(event.getMessage().getAuthor().getRoleColor().get());
            embed.setThumbnail(event.getMessageAuthor().getAvatar());
            event.getChannel().sendMessage(embed);
            return;
        }

        int i = 0;
        String map_name = command[1];

        QueryResult rs = world.db.jdbc.query("SELECT * FROM maps WHERE Name LIKE ? AND Staff = 0 LIMIT 10", "%" + map_name.toLowerCase() + "%");

        while (rs.next()) {
            int id = rs.getInt("id");
            String Name = rs.getString("Name");
            String MaxPlayers = rs.getString("MaxPlayers");
            String ReqLevel = rs.getString("ReqLevel");

//            MapName.add(Name);

//            embed.addField("\u0000", Name, true);
//            embed.addField("\u0000", MaxPlayers, true);
//            embed.addField("\u0000", ReqLevel, true);

//            embed.addInlineField(Name, "`Max Players:` " + MaxPlayers);
            embed.addField(
                    (i + 1) + ". " + Name,
                    "**Max Players:** " + MaxPlayers
                            + "\n" +
                    "**Required Level:** " + ReqLevel
                            + "\n" + "\u0000",
                    false
            );

//            embed.addInlineField("Name", "[" + Name + "](" + id + ")");
//            embed.addInlineField("Max Players", " " + MaxPlayers);
//            embed.addInlineField("Required Level", " " + ReqLevel);
            ++i;
        }
        rs.close();

//        embed.setColor(event.getMessage().getAuthor().getRoleColor().get());
//        embed.setThumbnail(event.getMessageAuthor().getAvatar());
//        embed.setAuthor(event.getMessageAuthor().getDiscriminatedName(), null, event.getMessageAuthor().getAvatar());
        embed.setDescription("Here is an accurate list of the searched keyword: '**" + map_name + "**'.\nThere are **" + i + "** results for your keyword: " + map_name + ".");
        embed.setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar());

        event.getChannel().sendMessage(embed);
    }
}
