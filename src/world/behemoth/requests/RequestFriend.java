package world.behemoth.requests;

import world.behemoth.aqw.Settings;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RequestFriend implements IRequest {
   public RequestFriend() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String username = params[0].toLowerCase();
      User client = world.zone.getUserByName(username);
      if(client == null) {
         throw new RequestException("Player \"" + username + "\" could not be found.");
      } else if(!Settings.isAllowed("bFriend", user, client)) {
         throw new RequestException(client.getName() + " is not accepting friend requests.");
      } else if(((Integer)client.properties.get("state")).intValue() == 2) {
         throw new RequestException(client.getName() + " is currently busy.");
      } else {
         Set requestedFriends = (Set)client.properties.get("requestedfriend");
         requestedFriends.add(Integer.valueOf(user.getUserId()));
         JSONArray friends = world.users.getFriends(user);
         Iterator friendRequest = friends.iterator();

         JSONObject friend;
         do {
            if(!friendRequest.hasNext()) {
               JSONObject friendRequest1 = new JSONObject();
               friendRequest1.put("cmd", "requestFriend");
               friendRequest1.put("unm", user.properties.get("username"));
               world.send(friendRequest1, client);
               world.send(new String[]{"server", "You have requested " + client.getName() + " to be friends."}, user);
               return;
            }

            Object o = friendRequest.next();
            friend = (JSONObject)o;
         } while(!friend.containsValue(client.properties.get("username")));

         throw new RequestException(client.getName() + " was already added to your friends list.");
      }
   }
}
