package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class DeclineFriend implements IRequest {
   public DeclineFriend() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = world.zone.getUserByName(params[0].toLowerCase());
      if(client != null) {
         if(user.properties.get("language").equals("BR")) {
            world.send(new String[]{"server", user.getName() + " recusou seu pedido de amizade."}, client);
         } else {
            world.send(new String[]{"server", user.getName() + " declined your friend request."}, client);
         }

      }
   }
}
