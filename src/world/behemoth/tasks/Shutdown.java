package world.behemoth.tasks;

import world.behemoth.world.World;

import java.util.concurrent.TimeUnit;

public class Shutdown implements Runnable {
   private World world;

   public Shutdown(World world) {
      super();
      this.world = world;
   }

   public void run() {
      try {
         this.world.send(new String[]{"server", "Server shutting down in 5 minutes."}, this.world.zone.getChannelList());
         Thread.sleep(TimeUnit.MINUTES.toMillis(4L));
         this.world.send(new String[]{"warning", "Server shutting down in 1 minute."}, this.world.zone.getChannelList());
         Thread.sleep(TimeUnit.MINUTES.toMillis(1L));
         this.world.send(new String[]{"logoutWarning", "", "60"}, this.world.zone.getChannelList());
         this.world.shutdown();
         Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
         System.exit(0);
      } catch (InterruptedException var2) {
         ;
      }

   }
}
