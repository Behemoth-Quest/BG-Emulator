package world.behemoth.dispatcher;

import org.javacord.api.event.message.MessageCreateEvent;

public class CommandException extends Exception {
    public CommandException(String text, MessageCreateEvent event) {
        event.getChannel().sendMessage(text);
    }
}