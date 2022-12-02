package world.behemoth.tasks;

import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;

import java.util.concurrent.TimeUnit;

public class Restart implements Runnable {
   private World world;

   public Restart(World world) {
      super();
      this.world = world;
   }

   public void run() {
      try {
         this.world.send(new String[]{"server", "Server restarting in 5 minutes."}, this.world.zone.getChannelList());
         Thread.sleep(TimeUnit.MINUTES.toMillis(4L));
         this.world.send(new String[]{"warning", "Server restarting in 1 minute."}, this.world.zone.getChannelList());
         Thread.sleep(TimeUnit.MINUTES.toMillis(1L));
         this.world.send(new String[]{"logoutWarning", "", "60"}, this.world.zone.getChannelList());
         this.world.shutdown();
         Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
         ExtensionHelper.instance().rebootServer();
      } catch (InterruptedException var2) {
         ;
      }

   }
}
