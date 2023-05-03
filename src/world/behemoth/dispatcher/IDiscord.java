package world.behemoth.dispatcher;

import org.javacord.api.event.message.MessageCreateEvent;
import world.behemoth.world.World;

public interface IDiscord
{
    void process(World paramWorld, MessageCreateEvent paramMessageCreateEvent) throws CommandException;
}
