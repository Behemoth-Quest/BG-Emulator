package world.behemoth.discord.utils;

import it.gotoandplay.smartfoxserver.SmartFoxServer;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import world.behemoth.db.objects.DiscordCommand;
import world.behemoth.dispatcher.CommandException;
import world.behemoth.dispatcher.IDiscord;
import world.behemoth.world.World;

import java.util.List;

public class Listener implements MessageCreateListener
{

    private World world;
    private int uAccess;

    public Listener(World world) {
        this.world = world;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String[] args = event.getMessage().getContent().split(" ");
        uAccess = 0;
        if (args[0].startsWith(world.discord.getPrefix())) {
//            SmartFoxServer.log.info("Recieved request from: " + event.getMessageAuthor().getDisplayName() + " - " + args[0].substring(1));
            List<Role> roles = event.getMessage().getAuthor().asUser().get().getRoles(event.getServer().get());
            if (event.getMessage().getAuthor().isBotOwner()) {
                uAccess = 4;
            } else if (event.getMessage().getAuthor().isServerAdmin()) {
                uAccess = 3;
            } else {
                uAccess = 1;
            }
            onCommand(event, args);
        }
    }

    private void onCommand(MessageCreateEvent event, String[] args) {
        DiscordCommand command = world.discord.commands.get(args[0].substring(1));
        if (command == null) { return; }
        if (uAccess < command.getAccess()) { return; }
//        SmartFoxServer.log.info(command.getFileName());
        try {
            Class<?> requestDefinition = Class.forName("world.behemoth.discord.commands." + command.getFileName());
            IDiscord request = (IDiscord) requestDefinition.newInstance();
            request.process(world, event);
        } catch (ClassNotFoundException var9) {
            SmartFoxServer.log.warning("Class not found: " + var9.getMessage());
        } catch (InstantiationException var10) {
            SmartFoxServer.log.warning("Instantiation error: " + var10.getMessage());
        } catch (IllegalAccessException var11) {
            SmartFoxServer.log.warning("Illegal access error: " + var11.getMessage());
        } catch (CommandException e) {
            SmartFoxServer.log.warning("Command exception: " + e.getMessage());
        }
    }

}