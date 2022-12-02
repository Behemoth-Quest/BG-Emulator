package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

import java.util.concurrent.TimeUnit;

public class RestoreTimed implements IRequest {
   public RestoreTimed() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      Long respawnTime = (Long)user.properties.get("respawntime");
      int elapsed = Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - respawnTime.longValue())).intValue();
      if(elapsed >= 8) {
         world.users.respawn(user);
         world.send(new String[]{"resTimed", user.properties.get("frame").toString(), user.properties.get("pad").toString()}, user);
      } else {
         world.users.log(user, "Packet Edit [RestoreTimed]", "Respawning when elapsed time is less than 8 seconds");
      }

   }
}
