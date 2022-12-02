package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class DragonBuff implements IRequest {
   public DragonBuff() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      world.send(new String[]{"Dragon Buff"}, user);
   }
}
