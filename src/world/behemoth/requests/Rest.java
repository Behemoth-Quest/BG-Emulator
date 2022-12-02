package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class Rest implements IRequest {
   public Rest() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int hp = ((Integer)user.properties.get("hp")).intValue();
      int maxHp = ((Integer)user.properties.get("hpmax")).intValue();
      int mp = ((Integer)user.properties.get("mp")).intValue();
      int maxMp = ((Integer)user.properties.get("mpmax")).intValue();
      if(((Integer)user.properties.get("state")).intValue() == 1 && (hp < maxHp || mp < maxMp)) {
         int newHp = (int)((double)hp + (double)maxHp * 0.1D);
         int newMp = (int)((double)mp + (double)maxMp * 0.1D);
         user.properties.put("hp", Integer.valueOf(newHp > maxHp?maxHp:newHp));
         user.properties.put("mp", Integer.valueOf(newMp > maxMp?maxMp:newMp));
         world.users.sendUotls(user, true, false, true, false, false, false);
      }

   }
}
