package world.behemoth.requests;

import world.behemoth.db.objects.House;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class HouseSave implements IRequest {
   public HouseSave() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      world.db.jdbc.run("UPDATE users SET HouseInfo = ? WHERE id = ?", new Object[]{params[0], user.properties.get("dbId")});
      Room house = world.zone.getRoomByName("house-" + user.properties.get("dbId"));
      if(house != null) {
         House houseObj = (House)world.areas.get(house.getName());
         if(houseObj != null) {
            houseObj.setHouseInfo(params[0]);
         }
      }
   }
}
