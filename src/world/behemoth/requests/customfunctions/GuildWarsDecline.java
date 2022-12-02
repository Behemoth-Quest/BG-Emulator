package world.behemoth.requests.customfunctions;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

import java.util.Set;

public class GuildWarsDecline implements IRequest {
   public GuildWarsDecline() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String userName = params[0].toLowerCase();
      User client = world.zone.getUserByName(userName);
      if(client != null) {
         Set requestedDuel = (Set)user.properties.get("requestedduel");
         requestedDuel.remove(Integer.valueOf(client.getUserId()));
         world.send(new String[]{"server", user.getName() + " declined guild wars challenge."}, client);
      }
   }
}
