package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class PvpQueueRequest implements IRequest {
   public PvpQueueRequest() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String warzone = params[0];
      JSONObject PVPQ = new JSONObject();
      PVPQ.put("cmd", "PVPQ");
      PVPQ.put("bitSuccess", Integer.valueOf(0));
      if(warzone.equals("none")) {
         world.warzoneQueue.removeUserFromQueues(Integer.valueOf(user.getUserId()));
         world.send(PVPQ, user);
         world.send(new String[]{"server", "You have been removed from the Warzone\'s queue"}, user);
      } else {
         world.warzoneQueue.queueUser(warzone, user);
         PVPQ.put("bitSuccess", Integer.valueOf(1));
         PVPQ.put("warzone", warzone);
         PVPQ.put("avgWait", Integer.valueOf(-1));
         world.send(PVPQ, user);
         world.send(new String[]{"server", "You joined the Warzone queue for " + warzone + "!"}, user);
      }

   }
}
