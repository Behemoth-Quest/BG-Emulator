package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RetrieveUserDatas implements IRequest {
   public RetrieveUserDatas() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      JSONObject iud = new JSONObject();
      iud.put("cmd", "initUserDatas");
      JSONArray a = new JSONArray();
      String[] arr$ = params;
      int len$ = params.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String userId = arr$[i$];
         User userObj = ExtensionHelper.instance().getUserById(Integer.parseInt(userId));
         if(userObj != null) {
            boolean addInfo = user.getUserId() == Integer.parseInt(userId);
            JSONObject userData = world.users.getUserData(Integer.parseInt(userId), addInfo);
            JSONObject userInfo = new JSONObject();
            userInfo.put("uid", Integer.valueOf(Integer.parseInt(userId)));
            userInfo.put("strFrame", user.properties.get("frame"));
            userInfo.put("strPad", user.properties.get("pad"));
            userInfo.put("data", userData);
            a.add(userInfo);
         }
      }

      iud.put("a", a);
      world.send(iud, user);
   }
}
