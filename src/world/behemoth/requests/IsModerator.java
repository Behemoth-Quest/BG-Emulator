package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class IsModerator implements IRequest {
   public IsModerator() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User target = world.zone.getUserByName(params[0].toLowerCase());
      if(target != null) {
         JSONObject isModerator = new JSONObject();
         isModerator.put("cmd", "isModerator");
         isModerator.put("val", Boolean.valueOf(target.isAdmin() || target.isModerator()));
         isModerator.put("unm", target.properties.get("username"));
         world.send(isModerator, user);
      }
   }
}
