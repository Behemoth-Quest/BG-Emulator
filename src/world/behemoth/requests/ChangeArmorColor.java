package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class ChangeArmorColor implements IRequest {
   public ChangeArmorColor() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int base = Integer.parseInt(params[0]);
      int trim = Integer.parseInt(params[1]);
      int accessory = Integer.parseInt(params[2]);
      world.db.jdbc.run("UPDATE users SET ColorBase = ?, ColorAccessory = ?, ColorTrim = ? WHERE id = ?", new Object[]{Integer.toHexString(base & 16777215).toUpperCase(), Integer.toHexString(accessory & 16777215).toUpperCase(), Integer.toHexString(trim & 16777215).toUpperCase(), user.properties.get("dbId")});
      JSONObject cac = new JSONObject();
      cac.put("uid", Integer.valueOf(user.getUserId()));
      cac.put("cmd", "changeArmorColor");
      cac.put("intColorBase", Integer.valueOf(base));
      cac.put("intColorTrim", Integer.valueOf(trim));
      cac.put("intColorAccessory", Integer.valueOf(accessory));
      world.sendToRoomButOne(cac, user, room);
      user.properties.put("colortrim", Integer.valueOf(trim));
      user.properties.put("colorbase", Integer.valueOf(base));
      user.properties.put("coloraccessory", Integer.valueOf(accessory));
   }
}
