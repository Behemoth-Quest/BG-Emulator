package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import net.sf.json.JSONObject;

public class RetrieveUserData implements IRequest {
   public RetrieveUserData() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      ExtensionHelper helper = ExtensionHelper.instance();
      int userId = Integer.parseInt(params[0]);
      JSONObject data = world.users.getUserData(userId, false);
      JSONObject iud = new JSONObject();
      User otherUser = helper.getUserById(userId);
      if(otherUser != null) {
         iud.put("cmd", "initUserData");
         iud.put("data", data);
         iud.put("strFrame", otherUser.properties.get("frame"));
         iud.put("strPad", otherUser.properties.get("pad"));
         iud.put("uid", Integer.valueOf(userId));
         world.send(iud, user);
      }
   }
}
