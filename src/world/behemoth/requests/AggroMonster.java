package world.behemoth.requests;

import world.behemoth.ai.MonsterAI;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AggroMonster implements IRequest {
   public AggroMonster() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(user != null) {
         ConcurrentHashMap monsters = (ConcurrentHashMap)room.properties.get("monsters");
         MonsterAI ai = (MonsterAI)monsters.get(Integer.valueOf(Integer.parseInt(params[0])));
         if(ai != null) {
            ai.addTarget(user.getUserId());
            if(ai.getState() == 1) {
               ai.setAttacking(world.scheduleTask(ai, 2500L, TimeUnit.MILLISECONDS, true));
            }

         }
      }
   }
}
