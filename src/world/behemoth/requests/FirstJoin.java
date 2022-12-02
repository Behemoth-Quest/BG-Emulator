package world.behemoth.requests;

import world.behemoth.config.ConfigData;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.sf.json.JSONObject;

public class FirstJoin implements IRequest {
   private static final List<String> exceptions = Arrays.asList(new String[]{"house", "deadlock"});
   public FirstJoin() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
    String lastArea = (String)user.properties.get("lastarea");
      this.sendCoreValues(user, world);
      String roomName = "faroff";
      String roomFrame = "Enter";
      String roomPad = "Spawn";
      world.rooms.basicRoomJoin(user, roomName, roomFrame, roomPad);
      world.db.jdbc.run("UPDATE servers SET Count = ? WHERE Name = ?", new Object[]{Integer.valueOf(world.zone.getUserCount()), ConfigData.SERVER_NAME});
   }

   private void sendCoreValues(User user, World world) {
      JSONObject cvu = new JSONObject();
      JSONObject o = new JSONObject();
      if(world.coreValues == null) {
         throw new RuntimeException("CVU is null!");
      } else {
         Iterator i$ = world.coreValues.entrySet().iterator();

         while(i$.hasNext()) {
            Entry e = (Entry)i$.next();
            o.put(e.getKey(), e.getValue());
         }

         cvu.put("cmd", "cvu");
         cvu.put("o", o);
         world.send(cvu, user);
      }
   }
}