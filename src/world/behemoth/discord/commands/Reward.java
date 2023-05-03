package world.behemoth.discord.commands;

import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.world.World;

import java.awt.*;

public class Reward implements IDiscord
{
    @Override
    public void process(World world, MessageCreateEvent event) throws CommandException
    {
        String[] command = event.getMessageContent().toLowerCase().split(" ");
        EmbedBuilder embed = new EmbedBuilder();

        if (command.length <= 1) {
            event.getChannel().sendMessage("input user id");
            return;
        }

        if (command.length == 2) {
            event.getChannel().sendMessage("input item id");
            return;
        }

        if (command.length == 3) {
            event.getChannel().sendMessage("input item quantity");
            return;
        }

        int UserID = Integer.parseInt(command[1]);
        int ItemID = Integer.parseInt(command[2]);
        int Quantity = Integer.parseInt(command[3]);

        Item item = world.items.get(ItemID);
        if (item == null) {
            event.getChannel().sendMessage("There is no such item as Item ID: " + ItemID);
            return;
        }

        String username = world.db.jdbc.queryForString("SELECT name FROM users WHERE id = ?", UserID);
        User user = world.zone.getUserByName(username.toLowerCase());
        if (user == null) {
            QueryResult itemResult = world.db.jdbc.query("SELECT id FROM users_items WHERE ItemID = ? AND UserID = ? AND Bank = 0", item.getId(), UserID);
            if (itemResult.next()) {
                int charItemId = itemResult.getInt("id");
                itemResult.close();
                if (item.getStack() > 1) {
                    int quantity = world.db.jdbc.queryForInt("SELECT Quantity FROM users_items WHERE id = ? FOR UPDATE", charItemId);
                    if (quantity < item.getStack()) {
                        world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE id = ?", quantity + Quantity, UserID);
                    }
                }
            } else {
                world.db.jdbc.run("INSERT INTO users_items (UserID, ItemID, EnhID, Equipped, Quantity, Bank, DatePurchased) VALUES (?, ?, ?, 0, ?, 0, NOW())", UserID, item.getId(), item.getEnhId(), Quantity);
            }
            itemResult.close();
        } else {
            world.send(new String[] {"server", "An in game staff has rewarded you the item " + item.getName() + "."}, user);
            world.users.dropItem(user, ItemID, Quantity);
        }

        embed.setDescription("The item **[" + world.clearHTMLTags(item.getName()) + "]('https://dashboard.behemothquest.online/')** has been rewarded to the user **[" + username + "]('https://dashboard.behemothquest.online/')**.");
        embed.setAuthor("Item Reward", null, event.getMessageAuthor().getAvatar());
        embed.setFooter("" + event.getMessageAuthor().getId());
        embed.setColor(Color.BLACK);
        embed.setThumbnail(event.getMessageAuthor().getAvatar());

        event.getChannel().sendMessage(embed);
    }
}
