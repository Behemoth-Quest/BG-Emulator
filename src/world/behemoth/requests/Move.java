package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class Move implements IRequest {
   public Move() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int x = Integer.parseInt(params[0]);
      int y = Integer.parseInt(params[1]);
      int speed = Integer.parseInt(params[2]);
      user.properties.put("tx", Integer.valueOf(x));
      user.properties.put("ty", Integer.valueOf(y));
      StringBuilder sb = new StringBuilder();
      sb.append("tx:");
      sb.append(x);
      sb.append(",ty:");
      sb.append(y);
      sb.append(",sp:");
      sb.append(speed);
      sb.append(",strFrame:");
      sb.append(user.properties.get("frame"));
      world.sendToRoomButOne(new String[]{"uotls", user.getName(), sb.toString()}, user, room);
   }
}
