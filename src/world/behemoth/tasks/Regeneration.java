package world.behemoth.tasks;

import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.concurrent.ScheduledFuture;
import net.sf.json.JSONObject;

public class Regeneration implements Runnable, CancellableTask {
   private User user;
   private World world;
   private ScheduledFuture<?> running;

   public Regeneration(User user, World world) {
      super();
      this.user = user;
      this.world = world;
   }

   public void run() {
      if(this.running != null && this.user != null) {
         int hp = ((Integer)this.user.properties.get("hp")).intValue();
         int maxHp = ((Integer)this.user.properties.get("hpmax")).intValue();
         int mp = ((Integer)this.user.properties.get("mp")).intValue();
         int maxMp = ((Integer)this.user.properties.get("mpmax")).intValue();
         if(((Integer)this.user.properties.get("state")).intValue() != 1 || hp >= maxHp && mp >= maxMp) {
            this.running.cancel(false);
         } else {
            int newHp = (int)((double)hp + (double)maxHp * 0.025D);
            int newMp = (int)((double)mp + (double)maxMp * 0.025D);
            this.user.properties.put("hp", Integer.valueOf(newHp >= maxHp?maxHp:newHp));
            this.user.properties.put("mp", Integer.valueOf(newMp >= maxMp?maxMp:newMp));
            JSONObject ct = new JSONObject();
            JSONObject p = new JSONObject();
            JSONObject pInfo = new JSONObject();
            pInfo.put("intHP", (Integer)this.user.properties.get("hp"));
            pInfo.put("intMP", (Integer)this.user.properties.get("mp"));
            p.put(this.user.getName(), pInfo);
            ct.put("cmd", "ct");
            ct.put("p", p);
            this.world.send(ct, this.world.zone.getRoom(this.user.getRoom()).getChannellList());
         }

      } else {
         throw new RuntimeException("regen handle is null, unable to continue.");
      }
   }

   public boolean isRegenerating() {
      return this.running != null && !this.running.isDone();
   }

   public void setRunning(ScheduledFuture<?> running) {
      if(this.running != null && !this.running.isDone()) {
         running.cancel(true);
      } else {
         this.running = running;
      }

   }

   public void cancel() {
      if(this.running != null) {
         this.running.cancel(false);
      }

   }
}
