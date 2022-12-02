package world.behemoth.requests.customfunctions;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class Rebirth implements IRequest {
   public Rebirth() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      Integer playerLevel = Integer.valueOf(Integer.parseInt(params[0]));
      Integer rebirthCount = (Integer)user.properties.get("rebirth");
      if(playerLevel.intValue() == 50 && ((Integer)user.properties.get("level")).intValue() == 50) {
         user.properties.put("rebirth", Integer.valueOf(rebirthCount.intValue() + 1));
         world.users.levelUp(user, 1);
         world.db.jdbc.run("UPDATE users SET Rebirth = (Rebirth + 1) WHERE id = ?", new Object[]{user.properties.get("dbId")});
         JSONObject UpdateRebirth = new JSONObject();
         UpdateRebirth.put("cmd", "updateRebirth");
         UpdateRebirth.put("intRebirth", (Integer)user.properties.get("rebirth"));
         world.send(UpdateRebirth, user);
         world.users.dropItem(user, 4, 1);
      } else if(playerLevel.intValue() == 50 && ((Integer)user.properties.get("level")).intValue() != 50) {
         world.send(new String[]{"server", "Hax ehuehue"}, user);
         world.users.kick(user);
      } else {
         world.send(new String[]{"server", "Your level isn\'t enough for rebirth"}, user);
      }

   }
}
