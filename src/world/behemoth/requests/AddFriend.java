package world.behemoth.requests;

import world.behemoth.config.ConfigData;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Set;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class AddFriend implements IRequest {
   public AddFriend() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = world.zone.getUserByName(params[0].toLowerCase());
      if(client != null) {
         Set userRequestedFriends = (Set)user.properties.get("requestedfriend");
         if(userRequestedFriends.contains(Integer.valueOf(client.getUserId()))) {
            userRequestedFriends.remove(Integer.valueOf(client.getUserId()));
            this.addFriend(user, client, world);
            this.addFriend(client, user, world);
         } else {
            world.users.kick(user);
            world.users.log(user, "Packet Edit [AddFriend]", "Forcing add friend.");
         }

      }
   }

   private void addFriend(User userObj1, User userObj2, World world) throws RequestException {
      int userID1 = ((Integer)userObj1.properties.get("dbId")).intValue();
      int userID2 = ((Integer)userObj2.properties.get("dbId")).intValue();
      JSONObject addFriend = new JSONObject();
      addFriend.put("cmd", "addFriend");
      QueryResult result = world.db.jdbc.query("SELECT * FROM users_friends WHERE UserID = ? AND FriendID = ?", new Object[]{Integer.valueOf(userID1), Integer.valueOf(userID2)});
      if(result.next()) {
         result.close();
         if(userObj1.properties.get("language").equals("BR")) {
            throw new RequestException(userObj2.getName() + " j\u00e1 foi adicionado \u00e0 sua lista de amigos.");
         } else {
            throw new RequestException(userObj2.getName() + " was already added to your friends list.");
         }
      } else {
         result.close();
         world.db.jdbc.run("INSERT INTO users_friends (UserID, FriendID) VALUES (?, ?)", new Object[]{Integer.valueOf(userID1), Integer.valueOf(userID2)});
         JSONObject friendInfo = new JSONObject();
         friendInfo.put("iLvl", (Integer)userObj2.properties.get("level"));
         friendInfo.put("ID", userObj2.properties.get("dbId"));
         friendInfo.put("sName", userObj2.properties.get("username"));
         friendInfo.put("sServer", ConfigData.SERVER_NAME);
         addFriend.put("friend", friendInfo);
         world.send(addFriend, userObj1);
         world.send(new String[]{"server", userObj2.getName() + " has been added to your friends list."}, userObj1);
      }
   }
}
