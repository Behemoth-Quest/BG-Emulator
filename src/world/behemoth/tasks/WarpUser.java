package world.behemoth.tasks;

import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.User;

import java.util.Iterator;
import java.util.Set;

public class WarpUser implements Runnable {
   private Set<User> users;
   private World world;

   public WarpUser(World world, Set<User> users) {
      super();
      this.users = users;
      this.world = world;
   }

   public void run() {
      Iterator i$ = this.users.iterator();

      while(i$.hasNext()) {
         User user = (User)i$.next();
         this.world.rooms.basicRoomJoin(user, "faroff");
      }

   }
}
