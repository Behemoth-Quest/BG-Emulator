package world.behemoth.requests;

import world.behemoth.db.objects.Hair;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class ChangeColor implements IRequest {
   public ChangeColor() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      JSONObject cc = new JSONObject();
      int skinColor = Integer.parseInt(params[0]);
      int hairColor = Integer.parseInt(params[1]);
      int eyeColor = Integer.parseInt(params[2]);
      int hairId = Integer.parseInt(params[3]);
      Hair hair = (Hair)world.hairs.get(Integer.valueOf(hairId));
      world.db.jdbc.run("UPDATE users SET ColorSkin = ?, ColorHair = ?, ColorEye = ?, HairID = ? WHERE id = ?", new Object[]{Integer.toHexString(skinColor & 16777215).toUpperCase(), Integer.toHexString(hairColor & 16777215).toUpperCase(), Integer.toHexString(eyeColor & 16777215).toUpperCase(), Integer.valueOf(hairId), user.properties.get("dbId")});
      cc.put("uid", Integer.valueOf(user.getUserId()));
      cc.put("cmd", "changeColor");
      cc.put("HairID", Integer.valueOf(hairId));
      cc.put("strHairName", hair.getName());
      cc.put("strHairFilename", hair.getFile());
      cc.put("intColorSkin", Integer.valueOf(skinColor));
      cc.put("intColorHair", Integer.valueOf(hairColor));
      cc.put("intColorEye", Integer.valueOf(eyeColor));
      world.sendToRoomButOne(cc, user, room);
      user.properties.put("hairId", Integer.valueOf(hairId));
      user.properties.put("colorhair", Integer.valueOf(hairColor));
      user.properties.put("colorskin", Integer.valueOf(skinColor));
      user.properties.put("coloreye", Integer.valueOf(eyeColor));
   }
}
