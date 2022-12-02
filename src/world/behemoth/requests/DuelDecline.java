package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Set;

public class DuelDecline implements IRequest {
   public DuelDecline() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = world.zone.getUserByName(params[0].toLowerCase());
      if(client != null) {
         Set requestedDuel = (Set)user.properties.get("requestedduel");
         requestedDuel.remove(Integer.valueOf(client.getUserId()));
         if(user.properties.get("language").equals("BR")) {
            world.send(new String[]{"server", user.getName() + " recusou seu desafio duelo."}, client);
         } else {
            world.send(new String[]{"server", user.getName() + " declined your duel challenge."}, client);
         }

      }
   }
}
