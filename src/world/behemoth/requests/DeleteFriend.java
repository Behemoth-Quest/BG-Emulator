package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class DeleteFriend implements IRequest {
   public DeleteFriend() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String friendName = params[1].toLowerCase();
      int friendId = world.db.jdbc.queryForInt("SELECT id FROM users WHERE Name = ?", new Object[]{friendName});
      this.deleteFriend(((Integer)user.properties.get("dbId")).intValue(), friendId, world);
      this.deleteFriend(friendId, ((Integer)user.properties.get("dbId")).intValue(), world);
      JSONObject deleteFriend = new JSONObject();
      deleteFriend.put("cmd", "deleteFriend");
      deleteFriend.put("ID", Integer.valueOf(friendId));
      world.send(deleteFriend, user);
      User friend = world.zone.getUserByName(friendName);
      if(friend != null) {
         deleteFriend.put("ID", (Integer)user.properties.get("dbId"));
         world.send(deleteFriend, friend);
      }
   }

   private void deleteFriend(int fromUser, int deleteId, World world) {
      world.db.jdbc.run("DELETE FROM users_friends WHERE UserID = ? AND FriendID = ?", new Object[]{Integer.valueOf(fromUser), Integer.valueOf(deleteId)});
   }
}
