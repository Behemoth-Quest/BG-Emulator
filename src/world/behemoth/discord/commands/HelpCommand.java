package world.behemoth.discord.commands;

import jdbchelper.QueryResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import world.behemoth.config.ConfigData;
import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.world.World;

import java.util.Iterator;
import java.util.List;

public class HelpCommand implements IDiscord
{
    @Override
    public void process(World world, MessageCreateEvent event) throws CommandException
    {
        EmbedBuilder embed = new EmbedBuilder();
        JSONArray commands = new JSONArray();
        int uAccess = 0;
        if (event.getMessage().getAuthor().isBotOwner()) {
            uAccess = 4;
        } else if (event.getMessage().getAuthor().isServerAdmin()) {
            uAccess = 3;
        } else if (event.getMessage().getAuthor().canManageServer()) {
            uAccess = 2;
        } else {
            uAccess = 1;
        }

        QueryResult rs = world.db.jdbc.query("SELECT * FROM discords_commands");
        while (rs.next()) {
            int Access = rs.getInt("Access");
            String Command = rs.getString("Command");
            String Description = rs.getString("Desc");
            String Parameters = rs.getString("Param");

            JSONObject command = new JSONObject();
            command.put("Access", Integer.valueOf(Access));
            command.put("Command", Command);
            command.put("Description", Description);
            command.put("Parameters", Parameters);
            commands.add(command);
        }
        rs.close();

        StringBuilder string = new StringBuilder();

        embed.setAuthor(event.getMessageAuthor().getDiscriminatedName(), null, event.getMessageAuthor().getAvatar());
        embed.setColor(event.getMessage().getAuthor().getRoleColor().get());
//        embed.setImage("https://cdn.discordapp.com/attachments/722140132085465169/873280500314288198/genshin-dance.gif");

        Iterator<?> iterate;

        if (uAccess >= 3) {
//            string.append("\n\n**Administrator Commands**");
            for (iterate = commands.iterator(); iterate.hasNext(); ) {
                JSONObject command = (JSONObject)iterate.next();
                if (command.get("Access").toString().equals("3")) {
//                    string.append(command.get("Command") + " " + command.get("Parameters") + "` - " + command.get("Description"));
                    embed.addField(command.get("Command") + " " + command.get("Parameters"), "\u0000" + command.get("Description"), false);
                }
            }
        }

        if (uAccess >= 2) {
//            string.append("\n\n**Moderator Commands**");
            for (iterate = commands.iterator(); iterate.hasNext(); ) {
                JSONObject command = (JSONObject)iterate.next();
                if (command.get("Access").toString().equals("2")) {
//                    string.append(command.get("Command") + " " + command.get("Parameters") + "` - " + command.get("Description"));
                    embed.addField(command.get("Command") + " " + command.get("Parameters"), "\u0000" + command.get("Description"), false);
                }
            }
        }

//        string.append("\n\n**Player Commands**");
        for (iterate = commands.iterator(); iterate.hasNext(); ) {
            JSONObject command = (JSONObject)iterate.next();
            if (command.get("Access").toString().equals("1")) {
//                string.append(command.get("Command") + " " + command.get("Parameters") + "` - " + command.get("Description"));
                embed.addField(command.get("Command") + " " + command.get("Parameters"), "\u0000" + command.get("Description"), false);
            }
        }

//        string.append("\n\n**Administrator Commands**");
//        for (iterate = commands.iterator(); iterate.hasNext(); ) {
//            JSONObject command = (JSONObject)iterate.next();
//            if (uAccess >= 3) {
//                string.append("\n`" + command.get("Command") + " " + command.get("Parameters") + "` - " + command.get("Description"));
//            }
//        }
//
//        string.append("\n\n**Moderator Commands**");
//        for (iterate = commands.iterator(); iterate.hasNext(); ) {
//            JSONObject command = (JSONObject)iterate.next();
//            if (uAccess == 2) {
//                string.append("\n`" + command.get("Command") + " " + command.get("Parameters") + "` - " + command.get("Description"));
//            }
//        }
//
//        string.append("\n\n**Player Commands**");
//        for (iterate = commands.iterator(); iterate.hasNext(); ) {
//            JSONObject command = (JSONObject)iterate.next();
//            if (uAccess == 1) {
//                string.append("\n`" + command.get("Command") + " " + command.get("Parameters") + "` - " + command.get("Description"));
//            }
//        }

        embed.setDescription(string.toString());
        event.getChannel().sendMessage(embed);
    }
}
