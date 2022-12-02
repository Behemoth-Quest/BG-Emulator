package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Random;
import net.sf.json.JSONObject;

public class Interaction implements IRequest {
   private static final Random rand = new Random();

   public Interaction() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      JSONObject ia = new JSONObject();
      ia.put("iAccessLevel", user.properties.get("access"));
      ia.put("cmd", "ia");
      ia.put("oName", params[1]);
      ia.put("typ", params[0]);
      ia.put("iUpgDays", Integer.valueOf(0));
      ia.put("unm", user.getName());
      if(params[0].equals("rval")) {
         ia.put("val", Integer.valueOf(rand.nextInt(9000)));
      } else if(params[0].equals("str")) {
         if(params[2].equals("aaaa")) {
            String username = params[3].toLowerCase();
            User client = world.zone.getUserByName(username);
            if(client == null) {
               throw new RequestException("Player \"" + username + "\" could not be found.");
            }

            world.rooms.basicRoomJoin(client, "hometree");
         }

         ia.put("val", params[2]);
      }

      world.sendToRoom(ia, user, room);
   }
}
