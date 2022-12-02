package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class MoveToCell implements IRequest {
   public MoveToCell() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String frame = params[0];
      String pad = params[1];
      user.properties.put("frame", frame);
      user.properties.put("pad", pad);
      user.properties.put("tx", Integer.valueOf(0));
      user.properties.put("ty", Integer.valueOf(0));
      if(((Integer)user.properties.get("state")).intValue() != 1) {
         world.users.regen(user);
         user.properties.put("state", Integer.valueOf(1));
         JSONObject sb = new JSONObject();
         JSONObject p = new JSONObject();
         JSONObject pInfo = new JSONObject();
         pInfo.put("intState", (Integer)user.properties.get("state"));
         p.put(user.getName(), pInfo);
         sb.put("cmd", "ct");
         sb.put("p", p);
         world.sendToRoom(sb, user, room);
      }

      StringBuilder sb1 = new StringBuilder();
      sb1.append("strPad:");
      sb1.append(user.properties.get("pad"));
      sb1.append(",tx:");
      sb1.append(user.properties.get("tx"));
      sb1.append(",strFrame:");
      sb1.append(user.properties.get("frame"));
      sb1.append(",ty:");
      sb1.append(user.properties.get("tx"));
      world.sendToRoomButOne(new String[]{"uotls", user.getName(), sb1.toString()}, user, room);
      user.properties.put("lastarea", room.getName() + "|" + frame + "|" + pad);
   }
}
