package world.behemoth.tasks;

import world.behemoth.ai.MonsterAI;
import world.behemoth.db.objects.Aura;
import world.behemoth.db.objects.AuraEffects;
import world.behemoth.world.World;
import world.behemoth.world.stats.Stats;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RemoveAura implements Runnable, CancellableTask {
   private World world;
   private Aura aura;
   private DamageOverTime dot;
   private ScheduledFuture<?> running;
   private User user;
   private MonsterAI ai;

   public RemoveAura(World world, Aura aura, MonsterAI ai) {
      super();
      this.world = world;
      this.aura = aura;
      this.ai = ai;
   }

   public RemoveAura(World world, Aura aura, User user) {
      super();
      this.world = world;
      this.aura = aura;
      this.user = user;
   }

   public void run() {
      JSONObject ct = new JSONObject();
      JSONArray a = new JSONArray();
      JSONObject o = new JSONObject();
      JSONObject auraInfo = new JSONObject();
      if(!this.aura.getCategory().isEmpty() && !this.aura.getCategory().equals("d")) {
         auraInfo.put("cat", this.aura.getCategory());
         if(this.aura.getCategory().equals("stun")) {
            auraInfo.put("s", "s");
         }
      }

      auraInfo.put("nam", this.aura.getName());
      o.put("cmd", "aura-");
      o.put("aura", auraInfo);
      if(this.user != null) {
         o.put("tInf", "p:" + this.user.getUserId());
      } else if(this.ai != null) {
         o.put("tInf", "m:" + this.ai.getMapId());
      }

      a.add(o);
      ct.put("cmd", "ct");
      ct.put("a", a);
      if(this.user != null) {
         Set auras = (Set)this.user.properties.get("auras");
         auras.remove(this);
         if(!this.aura.effects.isEmpty()) {
            Stats stats = (Stats)this.user.properties.get("stats");
            HashSet auraEffects = new HashSet();
            Iterator i$ = this.aura.effects.iterator();

            while(i$.hasNext()) {
               int effectId = ((Integer)i$.next()).intValue();
               AuraEffects ae = (AuraEffects)this.world.effects.get(Integer.valueOf(effectId));
               stats.effects.remove(ae);
               auraEffects.add(ae);
            }

            stats.update();
            stats.sendStatChanges(stats, auraEffects);
         }

         this.world.send(ct, this.world.zone.getRoom(this.user.getRoom()).getChannellList());
      } else if(this.ai != null) {
         this.ai.removeAura(this);
         this.world.send(ct, this.ai.getRoom().getChannellList());
      }

      if(this.dot != null) {
         this.dot.cancel();
      }

   }

   public void cancel() {
      if(this.dot != null) {
         this.dot.cancel();
      }

      if(this.running != null) {
         this.running.cancel(false);
      }

   }

   public void setRunning(ScheduledFuture<?> running) {
      this.running = running;
   }

   public Aura getAura() {
      return this.aura;
   }

   public void setDot(DamageOverTime dot) {
      this.dot = dot;
   }

   public int hashCode() {
      return this.aura.getId() + this.running.hashCode();
   }

   public boolean equals(Object obj) {
      if(obj == null) {
         return false;
      } else if(this.getClass() != obj.getClass()) {
         return false;
      } else {
         RemoveAura other = (RemoveAura)obj;
         return this.aura.getId() == other.aura.getId() || this.aura != null && this.aura.equals(other.aura)?this.running.hashCode() == other.running.hashCode() || this.running != null && this.running.equals(other.running):false;
      }
   }
}
