package world.behemoth.tasks;

import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;

public class KickUser implements Runnable {
   private User user;
   private World world;
   private ExtensionHelper helper;

   public KickUser(User user, World world) {
      super();
      this.user = user;
      this.world = world;
      this.helper = ExtensionHelper.instance();
   }

   public void run() {
      try {
         this.world.send(new String[]{"logoutWarning", "", "60"}, this.user);
         Thread.sleep(1000L);
         this.helper.disconnectUser(this.user);
         SmartFoxServer.log.info("User [ " + this.user.getName() + " ] has been kicked.");
      } catch (InterruptedException var2) {
         ;
      }

   }
}
